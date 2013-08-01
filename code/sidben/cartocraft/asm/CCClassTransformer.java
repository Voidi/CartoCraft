package sidben.cartocraft.asm;


import java.io.File;
import java.io.InputStream;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;




public class CCClassTransformer implements IClassTransformer {


    /*
     * From: http://www.minecraftforum.net/topic/1854988-tutorial-162-changing-vanilla-without-editing-base-classes-coremods-and-events-very-advanced/
     * 
     * ...with arg0 being the name of the class forge is about to work with at the JVM level
     * arg1 is the new name of the class (not sure what this is tbh)
     * arg2 you can look at it as the chuck of bytecode that's about to be loaded into JVM.
     */
    @Override
    public byte[] transform(String arg0, String arg1, byte[] arg2) {

        if (arg0.equals("abq")) {
            System.out.println("********* INSIDE OBFUSCATED EXPLOSION TRANSFORMER ABOUT TO PATCH: " + arg0);
            return this.patchClassASM(arg0, arg2, true);
        }

        if (arg0.equals("net.minecraft.world.Explosion")) {
            System.out.println("********* INSIDE EXPLOSION TRANSFORMER ABOUT TO PATCH: " + arg0);
            return this.patchClassASM(arg0, arg2, false);
        }

        // Check if the JVM is about to process the avs.class or the MapItemRenderer.class
        if (arg0.equals("avs") || arg0.equals("net.minecraft.client.gui.MapItemRenderer")) {
            System.out.println("********* INSIDE MAPITEMRENDERER TRANSFORMER ABOUT TO PATCH: " + arg0);
            arg2 = this.patchClassInJar(arg0, arg2, arg0, CCFMLLoadingPlugin.location);
        }

        return arg2;

    }


    public byte[] patchClassASM(String name, byte[] bytes, boolean obfuscated) {

        String targetMethodName = "";

        // Our target method
        if (obfuscated == true) {
            targetMethodName = "a";
        }
        else {
            targetMethodName = "doExplosionB";
        }


        // set up ASM class manipulation stuff. Consult the ASM docs for details
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        // Now we loop over all of the methods declared inside the Explosion class until we get to the targetMethodName "doExplosionB"

        @SuppressWarnings("unchecked")
        Iterator<MethodNode> methods = classNode.methods.iterator();
        while (methods.hasNext()) {
            MethodNode m = methods.next();
            int fdiv_index = -1;

            // Check if this is doExplosionB and it's method signature is (Z)V which means that it accepts a boolean (Z) and returns a void (V)
            if (m.name.equals(targetMethodName) && m.desc.equals("(Z)V")) {
                System.out.println("********* Inside target method!");

                AbstractInsnNode currentNode = null;
                @SuppressWarnings("unchecked")
                Iterator<AbstractInsnNode> iter = m.instructions.iterator();

                int index = -1;

                // Loop over the instruction set and find the instruction FDIV which does the division of 1/explosionSize
                while (iter.hasNext()) {
                    index++;
                    currentNode = iter.next();

                    // Found it! save the index location of instruction FDIV and the node for this instruction
                    if (currentNode.getOpcode() == Opcodes.FDIV) {
                        fdiv_index = index;
                    }
                }

                // now we want the save nods that load the variable explosionSize and the division instruction:

                /*
                 * mv.visitInsn(FCONST_1);
                 * mv.visitVarInsn(ALOAD, 0);
                 * mv.visitFieldInsn(GETFIELD, "net/minecraft/src/Explosion", "explosionSize", "F");
                 * mv.visitInsn(FDIV);
                 * mv.visitInsn(ICONST_0);
                 * mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/src/Block", "dropBlockAsItemWithChance", "(Lnet/minecraft/src/World;IIIIFI)V");
                 */

                AbstractInsnNode remNode1 = m.instructions.get(fdiv_index - 2); // mv.visitVarInsn(ALOAD, 0);
                AbstractInsnNode remNode2 = m.instructions.get(fdiv_index - 1); // mv.visitFieldInsn(GETFIELD, "net/minecraft/src/Explosion", "explosionSize", "F");
                AbstractInsnNode remNode3 = m.instructions.get(fdiv_index); // mv.visitInsn(FDIV);


                // just remove these nodes from the instruction set, this will prevent the instruction FCONST_1 to be divided.

                m.instructions.remove(remNode1);
                m.instructions.remove(remNode2);
                m.instructions.remove(remNode3);


                // in this section, i'll just illustrate how to inject a call to a static method if your instruction is a little more advanced than just removing a couple of instruction:

                /*
                 * To add new instructions, such as calling a static method can be done like so:
                 * 
                 * // make new instruction list
                 * InsnList toInject = new InsnList();
                 * 
                 * //add your own instruction lists: *USE THE ASM JAVADOC AS REFERENCE*
                 * toInject.add(new VarInsnNode(ALOAD, 0));
                 * toInject.add(new MethodInsnNode(INVOKESTATIC, "mod/culegooner/MyStaticClass", "myStaticMethod", "()V"));
                 * 
                 * // add the added code to the nstruction list
                 * // You can also choose if you want to add the code before or after the target node, check the ASM Javadoc (insertBefore)
                 * m.instructions.insert(targetNode, toInject);
                 */

                System.out.println("Patching Complete!");
                break;
            }
        }

        // ASM specific for cleaning up and returning the final bytes for JVM processing.
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
    }



    public byte[] patchClassInJar(String name, byte[] bytes, String ObfName, File location) {
        System.out.println("CCClassTransformer.patchClassInJar(" +name+ ", bytes[], " +ObfName+ ", location)");
        System.out.println("    Location: " + location.getAbsolutePath());

        
        try {
            // open the jar as zip
            ZipFile zip = new ZipFile(location);
           
            

            // find the file inside the zip that is called avs.class or net.minecraft.client.gui.MapItemRenderer.class
            // replacing the . to / so it would look for net/minecraft/client/gui/MapItemRenderer.class
            ZipEntry entry = zip.getEntry(name.replace('.', '/') + ".class");


            if (entry == null) {
                System.out.println(name + " not found in " + location.getName());
            }
            else {

                // serialize the class file into the bytes array
                InputStream zin = zip.getInputStream(entry);
                bytes = new byte[(int) entry.getSize()];
                zin.read(bytes);
                zin.close();
                System.out.println("[" + "CartoCraftCore" + "]: " + "Class " + name + " patched!");
            }
            zip.close();
        }
        catch (Exception e) {
            throw new RuntimeException("Error overriding " + name + " from " + location.getName(), e);
        }

        // return the new bytes
        return bytes;
    }

}
