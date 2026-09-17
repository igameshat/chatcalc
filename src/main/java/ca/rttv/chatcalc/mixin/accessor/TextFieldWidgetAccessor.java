package ca.rttv.chatcalc.mixin.accessor;

import ca.rttv.chatcalc.display.ChatScreenDisplay;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EditBox.class)
abstract class TextFieldWidgetAccessor extends AbstractWidget {
	private TextFieldWidgetAccessor(int x, int y, int width, int height, Component message) {
		super(x, y, width, height, message);
		throw new UnsupportedOperationException("Mixin shouldn't be instantiated");
	}

	// Using a mixin instead of ScreenEvents because the cursor location is a local value, and it's a lot of code to copy over
	@Inject(method = "extractWidgetRenderState", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Ljava/lang/String;isEmpty()Z", ordinal = 1))
	private void chatcalc$renderWidget(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci, @Local(ordinal = 6) int m) {
		if (!(getMessage().getContents() instanceof TranslatableContents translatable && translatable.getKey().equals("chat.editBox")))
			return;

		// No need to check the screen as the instance will only be set if the screen is a ChatScreen
		var instance = ChatScreenDisplay.Companion.getInstance();
		if (instance == null) return;
		if (instance.shouldRender()) instance.render(context, m - 8);
	}
}
