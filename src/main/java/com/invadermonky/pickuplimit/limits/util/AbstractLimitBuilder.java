package com.invadermonky.pickuplimit.limits.util;

import baubles.api.IBauble;
import com.invadermonky.pickuplimit.limits.api.ILimitFunction;
import com.invadermonky.pickuplimit.util.LogHelper;
import com.invadermonky.pickuplimit.util.libs.ModIds;
import gnu.trove.map.hash.THashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractLimitBuilder <T extends AbstractLimitBuilder<T,S>, S extends AbstractLimitGroup<?>>{
    protected final String groupName;
    protected final int defaultLimit;
    protected final NonNullList<ItemStack> groupStacks = NonNullList.create();
    protected ILimitFunction itemLimitFunction;
    protected String pickupLimitMessage;
    protected final Map<ItemStack, Integer> armorLimits = new HashMap<>();
    protected final Map<ItemStack, Integer> baubleLimits = new HashMap<>();
    protected final LinkedHashMap<String,Integer> stageLimitOverride = new LinkedHashMap<>();
    protected final THashMap<String, NonNullList<ItemStack>> stagedStackRemovals = new THashMap<>();

    public AbstractLimitBuilder(String groupName, int defaultLimit) {
        this.groupName = groupName;
        this.defaultLimit = defaultLimit;
        this.itemLimitFunction = null;
    }

    protected abstract T getThis();

    /**
     * Returns the builder pickup limit group name
     */
    public String getGroupName() {
        return this.groupName;
    }

    /**
     * Returns the default group pickup limit
     */
    public int getDefaultLimit() {
        return this.defaultLimit;
    }

    /**
     * Returns all ItemStacks that have a limited pickup defined by this pickup limit
     */
    public NonNullList<ItemStack> getGroupStacks() {
        return this.groupStacks;
    }

    /**
     * Adds all passed ItemStacks to the group ItemStack list.
     *
     * @param stacks ItemStacks to add
     * @return this
     */
    public T addStacksToGroup(ItemStack... stacks) {
        this.groupStacks.addAll(Arrays.asList(stacks));
        return this.getThis();
    }

    /**
     * Sets a new failed pickup message for this group. This can be plain text or a language key. Including a %s will
     * cause the value to include the display name of the ItemStack.
     *
     * @param limitMessage The new pickup message text or language key
     * @return this
     */
    public T setLimitMessage(String limitMessage) {
        if(limitMessage != null) {
            this.pickupLimitMessage = limitMessage;
        }
        return this.getThis();
    }

    /**
     * Returns the defined pickup limit message string that will be displayed in the pickup fails. This can be a localization
     * key or plain text.
     *
     * @return The pickup limit message string that will be displayed if the pickup fails.
     */
    public String getPickupLimitMessage() {
        return this.pickupLimitMessage;
    }

    public T setItemLimitValueFunction(ILimitFunction function) {
        this.itemLimitFunction = function;
        return getThis();
    }

    @Nullable
    public ILimitFunction getItemLimitFunction() {
        return this.itemLimitFunction;
    }

    /**
     * Adds a limit adjustment when the player has the passed armor equipped. This adjustment value can be positive or
     * negative.
     *
     * @param stack The armor ItemStack
     * @param adjustment The limit adjustment when the armor is equipped
     * @return
     */
    public T addArmorLimitAdjustment(ItemStack stack, int adjustment) {
        if(!stack.isEmpty()) {
            this.armorLimits.put(stack, adjustment);
        } else {
            LogHelper.error("Error adding armor limit for " + stack.getItem().getRegistryName());
        }
        return this.getThis();
    }

    /**
     * Adds a limit adjustment when the player has the passed bauble equipped. This adjustment value can be positive
     * or negative.
     *
     * @param stack The bauble ItemStack
     * @param adjustment The limit adjustment when the bauble is equipped
     * @return this
     */
    @Optional.Method(modid = ModIds.ConstIds.gamestages)
    public T addBaubleLimitAdjustment(ItemStack stack, int adjustment) {
        if(!stack.isEmpty() && stack.getItem() instanceof IBauble) {
            baubleLimits.put(stack, adjustment);
        } else {
            LogHelper.error("Error adding bauble limit for " + stack.getItem().getRegistryName());
        }
        return this.getThis();
    }

    /**
     * Returns any defined equipped armor pickup limit adjustments.
     */
    public Map<ItemStack, Integer> getArmorLimits() {
        return this.armorLimits;
    }

    /**
     * Returns any defined bauble pickup limit adjustments.
     */
    @Optional.Method(modid = ModIds.ConstIds.gamestages)
    public Map<ItemStack, Integer> getBaubleLimits() {
        return this.baubleLimits;
    }

    /**
     * Creates a game stage pickup limit override. This value will override the default pickup limit. Stages <b>must</b>
     * be added in order. The last stage added will override any values before it provided the player has the required
     * stage.
     *
     * @param stageName The GameStage stage name
     * @param limitOverride The pickup limit override for this stage
     * @return this
     */
    @Optional.Method(modid = ModIds.ConstIds.gamestages)
    public T addStageLimitOverride(String stageName, int limitOverride) {
        this.stageLimitOverride.put(stageName, limitOverride);
        return this.getThis();
    }

    /**
     * Returns any defined GameStage group pickup limit overrides.
     */
    @Optional.Method(modid = ModIds.ConstIds.gamestages)
    public LinkedHashMap<String,Integer> getStageLimitOverride() {
        return this.stageLimitOverride;
    }

    /**
     * Specifies any ItemStacks that will be removed from this group once the player obtains the specified GameStage.
     *
     * @param stageName The name of the GameStage
     * @param stacks Any stacks that will be removed from this group limit when the player obtains the specified GameStage
     * @return this
     */
    @Optional.Method(modid = ModIds.ConstIds.gamestages)
    public T addStagedStackGroupRemoval(String stageName, ItemStack... stacks) {
        NonNullList<ItemStack> stackList = NonNullList.from(ItemStack.EMPTY, stacks);
        stackList.removeIf(ItemStack::isEmpty);
        if(this.stagedStackRemovals.containsKey(stageName)) {
            this.stagedStackRemovals.get(stageName).addAll(stackList);
        } else {
            this.stagedStackRemovals.put(stageName, stackList);
        }
        return this.getThis();
    }

    /**
     * Returns any defined GameStage group ItemStack removals.
     */
    @Optional.Method(modid = ModIds.ConstIds.gamestages)
    public THashMap<String, NonNullList<ItemStack>> getStagedStackRemovals() {
        return this.stagedStackRemovals;
    }

    public abstract S build();
}
