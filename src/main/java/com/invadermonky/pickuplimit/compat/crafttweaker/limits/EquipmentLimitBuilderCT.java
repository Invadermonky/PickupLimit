package com.invadermonky.pickuplimit.compat.crafttweaker.limits;

import com.invadermonky.pickuplimit.compat.crafttweaker.api.ILimitFunctionCT;
import com.invadermonky.pickuplimit.compat.crafttweaker.api.ILimitGroupCT;
import com.invadermonky.pickuplimit.limits.builders.EquipmentLimitBuilder;
import com.invadermonky.pickuplimit.registry.LimitRegistry;
import com.invadermonky.pickuplimit.util.libs.LibZenClasses;
import com.invadermonky.pickuplimit.util.libs.ModIds;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.enchantments.IEnchantmentDefinition;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass(LibZenClasses.EquipmentLimitBuilder)
public class EquipmentLimitBuilderCT {
    private EquipmentLimitBuilder builder;

    public EquipmentLimitBuilderCT(String groupName, int defaultLimit) {
        this.builder = new EquipmentLimitBuilder(groupName, defaultLimit);
    }

    @ZenMethod
    public EquipmentLimitBuilderCT addStacks(IItemStack... stacks) {
        this.builder.addStacksToGroup(CraftTweakerMC.getItemStacks(stacks));
        return this;
    }

    @ZenMethod
    public EquipmentLimitBuilderCT addEnchantments(IEnchantmentDefinition... iEnchantments) {
        Enchantment[] enchants = new Enchantment[iEnchantments.length];
        for(int i = 0; i < enchants.length; i++) {
            enchants[i] = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(iEnchantments[i].getRegistryName()));
        }
        this.builder.addEnchantsToGroup(enchants);
        return this;
    }

    @ZenMethod
    public EquipmentLimitBuilderCT setMatchAnyEnchant() {
        this.builder.setMatchAnyEnchant();
        return this;
    }

    @ZenMethod
    public EquipmentLimitBuilderCT setIgnoreItemEnchantmentCount() {
        this.builder.setIgnoreItemEnchantmentCount();
        return this;
    }

    @ZenMethod
    public EquipmentLimitBuilderCT setIgnoreEnchantmentLevel() {
        this.builder.setIgnoreEnchantmentLevel();
        return this;
    }

    @ZenMethod
    public EquipmentLimitBuilderCT setStackLimitFunction(ILimitFunctionCT function) {
        this.builder.setItemLimitValueFunction((player, stack, cache) -> function.process(
                CraftTweakerMC.getIPlayer(player),
                CraftTweakerMC.getIItemStack(stack),
                new ILimitGroupCT() {
                    @Override
                    public String getName() {
                        return cache.getName();
                    }

                    @Override
                    public String getMessage() {
                        return cache.getLimitMessage();
                    }

                    @Override
                    public int getLimit() {
                        return cache.getLimit();
                    }

                    @Override
                    public int getStackLimitValue(IItemStack stack) {
                        return cache.getStackLimitValue(CraftTweakerMC.getItemStack(stack));
                    }
                }
        ));
        return this;
    }

    @ZenMethod
    public EquipmentLimitBuilderCT setCheckMainhand() {
        this.builder.setCheckMainhand();
        return this;
    }

    @ZenMethod
    public EquipmentLimitBuilderCT setCheckOffhand() {
        this.builder.setCheckOffhand();
        return this;
    }

    @ZenMethod
    public EquipmentLimitBuilderCT setLimitMessage(String message) {
        this.builder.setLimitMessage(message);
        return this;
    }

    @ZenMethod
    public EquipmentLimitBuilderCT addArmorLimitAdjustment(IItemStack armorStack, int adjustment) {
        this.builder.addArmorLimitAdjustment(CraftTweakerMC.getItemStack(armorStack), adjustment);
        return this;
    }

    @ZenMethod
    @Optional.Method(modid = ModIds.ConstIds.baubles)
    public EquipmentLimitBuilderCT addBaubleLimitAdjustment(IItemStack baubleStack, int adjustment) {
        this.builder.addBaubleLimitAdjustment(CraftTweakerMC.getItemStack(baubleStack), adjustment);
        return this;
    }

    @ZenMethod
    @Optional.Method(modid = ModIds.ConstIds.gamestages)
    public EquipmentLimitBuilderCT addStageLimitOverride(String stageName, int limitOverride) {
        this.builder.addStageLimitOverride(stageName, limitOverride);
        return this;
    }

    @ZenMethod
    @Optional.Method(modid = ModIds.ConstIds.gamestages)
    public EquipmentLimitBuilderCT addStagedStackRemovals(String stageName, IItemStack... stacks) {
        this.builder.addStagedStackGroupRemoval(stageName, CraftTweakerMC.getItemStacks(stacks));
        return this;
    }

    @ZenMethod
    @Optional.Method(modid = ModIds.ConstIds.gamestages)
    public EquipmentLimitBuilderCT addStagedEnchantmentRemovals(String stageName, IEnchantmentDefinition... iEnchantments) {
        Enchantment[] enchants = new Enchantment[iEnchantments.length];
        for(int i = 0; i < enchants.length; i++) {
            enchants[i] = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(iEnchantments[i].getRegistryName()));
        }
        this.builder.addStagedEnchantRemoval(stageName, enchants);
        return this;
    }

    @ZenMethod
    public void build() {
        LimitRegistry.addEquipmentLimitGroup(this.builder.build());
    }
}
