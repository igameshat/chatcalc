package ca.rttv.chatcalc.display

import ca.rttv.chatcalc.ChatCalc.tryParse
import ca.rttv.chatcalc.ChatHelper
import ca.rttv.chatcalc.mixin.accessor.ChatInputSuggesterAccessor
import ca.rttv.chatcalc.mixin.accessor.ChatScreenAccessor
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.ChatScreen
import net.minecraft.network.chat.Component

class ChatScreenDisplay(val chatField: EditBox, val suggester: ChatInputSuggesterAccessor) : DisplayAbove() {
	override var x = 0 // This is set by the mixin
	override val y get() = chatField.y - 4
	override val centered = false

	override fun parseWord(): String = ChatHelper.getSection(chatField.message.toString(), chatField.cursorPosition)

	override fun allowKeyPress(keycode: Int): Boolean = suggester.pendingSuggestions.let { suggestions ->
		super.allowKeyPress(keycode)
				|| suggestions == null
				|| !suggestions.isDone
				|| !suggestions.isCompletedExceptionally
				|| !suggestions.getNow(null).isEmpty
				|| !tryParse(chatField.message.toString(), chatField.cursorPosition) { chatField.setMessage(Component.literal(it)) }
	}

	companion object {
		var instance: ChatScreenDisplay? = null
			private set

		fun init() {
			ScreenEvents.AFTER_INIT.register { _, screen, _, _ ->
				if (screen !is ChatScreen) return@register
				screen as ChatScreenAccessor
				instance = ChatScreenDisplay(screen.chatField, screen.chatInputSuggestor as ChatInputSuggesterAccessor)
				ScreenKeyboardEvents.allowKeyPress(screen).register { _, keyInput, ->
					instance!!.allowKeyPress(keyInput.keycode)
				}
			}
		}
	}
}