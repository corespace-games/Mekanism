package mekanism.common.capabilities.chemical.item;

import java.util.function.Consumer;
import javax.annotation.Nonnull;
import mekanism.api.NBTConstants;
import mekanism.api.chemical.infuse.IInfusionHandler.IMekanismInfusionHandler;
import mekanism.api.chemical.infuse.IInfusionTank;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.resolver.BasicCapabilityResolver;
import mekanism.common.capabilities.resolver.ICapabilityResolver;

/**
 * Helper class for implementing infusion handlers for items
 */
public abstract class ItemStackMekanismInfusionHandler extends ItemStackMekanismChemicalHandler<InfuseType, InfusionStack, IInfusionTank> implements IMekanismInfusionHandler {

    @Nonnull
    @Override
    protected String getNbtKey() {
        return NBTConstants.INFUSION_TANKS;
    }

    @Override
    protected void gatherCapabilityResolvers(Consumer<ICapabilityResolver> consumer) {
        consumer.accept(BasicCapabilityResolver.constant(Capabilities.INFUSION_HANDLER_CAPABILITY, this));
    }
}