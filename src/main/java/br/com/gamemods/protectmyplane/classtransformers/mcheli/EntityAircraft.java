package br.com.gamemods.protectmyplane.classtransformers.mcheli;

import br.com.gamemods.protectmyplane.annotation.Hook;
import br.com.gamemods.protectmyplane.event.PlayerSpawnVehicleEvent;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.MinecraftForge;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.GeneratorAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class EntityAircraft implements IClassTransformer
{
    @Override
    public byte[] transform(String name, String srgName, byte[] bytes)
    {
        if (srgName.equals("mcheli.aircraft.MCH_EntityAircraft"))
        {
            System.out.println("----------> Patching mcheli.aircraft.EntityAircraft");
            ClassReader reader = new ClassReader(bytes);
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

            addPmpOwner(writer, srgName);

            reader.accept(writer, ClassReader.EXPAND_FRAMES);
            bytes = writer.toByteArray();
            FileOutputStream out = null;
            try
            {
                File file = new File(srgName + ".class");
                System.out.println("----------> Saving to "+file.getAbsolutePath());
                out = new FileOutputStream(file);
                out.write(bytes);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                if(out != null) try
                {
                    out.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            return bytes;
        }
        else if (srgName.equals("mcheli.aircraft.MCH_ItemAircraft"))
        {
            System.out.println("----------> Patching mcheli.aircraft.MCH_ItemAircraft");
            ClassReader reader = new ClassReader(bytes);
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

            System.out.println("----------> ASM Initialized");
            ClassVisitor visitor = addSpawnCheck(writer, srgName);
            reader.accept(visitor, ClassReader.EXPAND_FRAMES);

            bytes = writer.toByteArray();
            FileOutputStream out = null;
            try
            {
                File file = new File(srgName + ".class");
                System.out.println("----------> Saving to "+file.getAbsolutePath());
                out = new FileOutputStream(file);
                out.write(bytes);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                if(out != null) try
                {
                    out.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            return bytes;
        }

        return bytes;
    }

    @Hook
    public static boolean hook(Object obj, ItemStack stack, EntityPlayer player, MovingObjectPosition position)
    {
        //System.out.println("PlayerSpawnVehicleEvent "+player+" "+stack+" "+" "+position+" "+stack);
        return MinecraftForge.EVENT_BUS.post(new PlayerSpawnVehicleEvent(player, stack, position.blockX, position.blockY, position.blockZ));
    }

    public static String getCommandSenderName(ICommandSender sender)
    {
        return sender.getCommandSenderName();
    }

    public static UUID getPersistentID(Entity entity)
    {
        return entity.getPersistentID();
    }

    private class SpawnCheckAdapter extends GeneratorAdapter
    {
        boolean waitingALoad0, waitingPop, patched;
        int var;

        protected SpawnCheckAdapter(MethodVisitor mv, int access, String name, String desc)
        {
            super(Opcodes.ASM4, mv, access, name, desc);
            var = newLocal(Type.getType("Lmcheli/aircraft/MCH_EntityAircraft;"));
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf)
        {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
            if(!patched && !waitingALoad0 && !waitingPop && opcode == Opcodes.INVOKESTATIC && name.equals("isHitTypeTile"))
            {
                waitingALoad0 = true;
                System.out.println("----------> Waiting ALOAD 0");
            }
        }

        @Override
        public void visitVarInsn(int opcode, int var)
        {
            if(waitingALoad0 && opcode == Opcodes.ALOAD && var == 0)
            {
                System.out.println("----------> ALOAD 0 found! Patching");
                super.visitVarInsn(Opcodes.ALOAD, 0);
                super.visitVarInsn(Opcodes.ALOAD, 1);
                super.visitVarInsn(Opcodes.ALOAD, 3);
                super.visitVarInsn(Opcodes.ALOAD, 23);
                System.out.println("----------> Aloads OK");
                super.visitMethodInsn(Opcodes.INVOKESTATIC,
                        "br/com/gamemods/protectmyplane/classtransformers/mcheli/EntityAircraft",
                        "hook",
                        "(Ljava/lang/Object;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/MovingObjectPosition;)Z",
                        false);
                System.out.println("----------> EntityAircraft.hook() OK");
                Label elseLabel = new Label();
                super.visitJumpInsn(Opcodes.IFEQ, elseLabel);
                System.out.println("----------> IFNE OK");
                super.visitVarInsn(Opcodes.ALOAD, 1);
                super.visitInsn(Opcodes.ARETURN);
                System.out.println("----------> return par1ItemStack OK");
                super.visitLabel(elseLabel);
                System.out.println("----------> Waiting POP");
                waitingALoad0 = false;
                waitingPop = true;
            }

            super.visitVarInsn(opcode, var);
        }

        @Override
        public void visitInsn(int opcode)
        {
            if(waitingPop && opcode == Opcodes.POP)
            {
                System.out.println("----------> POP Found! Patching");
                String mine = EntityAircraft.class.getName().replace('.','/');
                super.visitInsn(Opcodes.DUP);
                super.visitVarInsn(Opcodes.ALOAD, 3);
                super.visitMethodInsn(Opcodes.INVOKESTATIC, mine, "getCommandSenderName", "(Lnet/minecraft/command/ICommandSender;)Ljava/lang/String;", false);
                //super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "mcheli/aircraft/MCH_EntityAircraft", "setPmpOwnerName", "(Ljava/lang/String;)V", false);
                super.visitFieldInsn(Opcodes.PUTFIELD, "mcheli/aircraft/MCH_EntityAircraft", "pmpOwnerName", "Ljava/lang/String;");
                super.visitVarInsn(Opcodes.ALOAD, 3);
                super.visitMethodInsn(Opcodes.INVOKESTATIC, mine, "getPersistentID", "(Lnet/minecraft/entity/Entity;)Ljava/util/UUID;", false);
                super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/UUID", "toString", "()Ljava/lang/String;", false);
                //super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "mcheli/aircraft/MCH_EntityAircraft", "setPmpOwnerId", "(Ljava/lang/String;)V", false);
                super.visitFieldInsn(Opcodes.PUTFIELD, "mcheli/aircraft/MCH_EntityAircraft", "pmpOwnerId", "Ljava/lang/String;");
                waitingPop = false;
                patched = true;
                System.out.println("----------> Patch completed!");
                return;
            }

            super.visitInsn(opcode);
        }
    }

    public ClassVisitor addSpawnCheck(ClassWriter writer, String srgName)
    {
        return new ClassVisitor(Opcodes.ASM4, writer)
        {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
            {
                MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
                if("func_77659_a".equals(name) || "onItemRightClick".equals(name))
                {
                    System.out.println("----------> Method onItemRightClick found!");
                    return new SpawnCheckAdapter(methodVisitor, access, name, desc);
                }

                return methodVisitor;
            }
        };
    }

    public void addPmpOwner(ClassWriter writer, String srgName)
    {
        String string = "Ljava/lang/String;";
        writer.visitField(Opcodes.ACC_PUBLIC, "pmpOwnerId", string, null, null).visitEnd();
        writer.visitField(Opcodes.ACC_PUBLIC, "pmpOwnerName", string, null, null).visitEnd();

        /* I really need to lean Mixin...
        MethodVisitor methodVisitor = writer.visitMethod(Opcodes.ACC_PUBLIC, "getPmpOwnerId", "()"+string, null, null);
        GeneratorAdapter adapter = new GeneratorAdapter(methodVisitor, Opcodes.ACC_PUBLIC, "getPmpOwnerId", "()"+string);
        adapter.visitVarInsn(Opcodes.ALOAD, 0);
        adapter.visitFieldInsn(Opcodes.GETFIELD, srgName.replace('.', '/'), "pmpOwnerId", string);
        adapter.visitInsn(Opcodes.ARETURN);
        adapter.visitEnd();

        methodVisitor = writer.visitMethod(Opcodes.ACC_PUBLIC, "getPmpOwnerName", "()"+string, null, null);
        adapter = new GeneratorAdapter(methodVisitor, Opcodes.ACC_PUBLIC, "getPmpOwnerName", "()"+string);
        adapter.visitVarInsn(Opcodes.ALOAD, 0);
        adapter.visitFieldInsn(Opcodes.GETFIELD, srgName.replace('.', '/'), "pmpOwnerName", string);
        adapter.visitInsn(Opcodes.ARETURN);
        adapter.visitEnd();

        methodVisitor = writer.visitMethod(Opcodes.ACC_PUBLIC, "setPmpOwnerId", "("+string+")V", null, null);
        adapter = new GeneratorAdapter(methodVisitor, Opcodes.ACC_PUBLIC, "setPmpOwnerId", "("+string+")V");
        adapter.visitVarInsn(Opcodes.ALOAD, 0);
        adapter.visitVarInsn(Opcodes.ALOAD, 1);
        adapter.visitFieldInsn(Opcodes.PUTFIELD, srgName.replace('.','/'), "pmpOwnerId", string);
        adapter.visitEnd();

        methodVisitor = writer.visitMethod(Opcodes.ACC_PUBLIC, "setPmpOwnerName", "("+string+")V", null, null);
        adapter = new GeneratorAdapter(methodVisitor, Opcodes.ACC_PUBLIC, "setPmpOwnerName", "("+string+")V");
        adapter.visitVarInsn(Opcodes.ALOAD, 0);
        adapter.visitVarInsn(Opcodes.ALOAD, 1);
        adapter.visitFieldInsn(Opcodes.PUTFIELD, srgName.replace('.','/'), "pmpOwnerName", string);
        adapter.visitEnd();
        */
    }
}