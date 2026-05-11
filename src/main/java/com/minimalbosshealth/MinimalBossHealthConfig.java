package com.minimalbosshealth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("minimalbosshealth")
public interface MinimalBossHealthConfig extends Config
{

	// region Sections

	@ConfigSection(
		name = "Bar Settings",
		description = "Settings that only apply to the bar display style",
		position = 4,
		closedByDefault = true
	)
	String barSettingsSection = "barSettings";

	// endregion

	// region General Settings

	@ConfigItem(
		keyName = "fadeEnabled",
		name = "Fade in/out",
		description = "Fade the health bar in when a boss appears and out when it dies",
		position = 0
	)
	default boolean fadeEnabled()
	{
		return true;
	}

	@ConfigItem(
			keyName = "hitpointsDisplayStyle",
			name = "Display style",
			description = "Show hitpoint's as value (if possible), percentage, or both",
			position = 1
	)
	default HitpointsDisplayStyle hitpointsDisplayStyle() { return HitpointsDisplayStyle.HITPOINTS; }

	@Alpha
	@ConfigItem(
		keyName = "remainingHpColor",
		name = "Remaining HP color",
		description = "The color of the boss's remaining health.",
		position = 2
	)
	default Color remainingHpColor() { return Color.GREEN; }

	@Alpha
	@ConfigItem(
		keyName = "missingHpColor",
		name = "Missing HP color",
		description = "The color of the boss's missing health.",
		position = 3
	)
	default Color missingHpColor() { return Color.RED; }

	// end region

	// region Bar Style

	@ConfigItem(
		keyName = "barHeight",
		name = "Health bar height",
		description = "The height of the boss's health bar.",
		section = barSettingsSection,
		position = 0
	)
	default int barHeight() { return 2; }

	@ConfigItem(
		keyName = "displayBarText",
		name = "Display text",
		description = "Show the opponent's health percentage value as text",
		section = barSettingsSection,
		position = 1
	)
	default DisplayBarTextOptions displayBarText() { return DisplayBarTextOptions.HOVER; }

	@ConfigItem(
		keyName = "barTextFontSize",
		name = "Font size",
		description = "Font size of the displayed text",
		section = barSettingsSection,
		position = 2
	)
	default int barTextFontSize() { return 16; }

	@ConfigItem(
		keyName = "barTextXOffset",
		name = "Text X offset",
		description = "X Offset of the displayed text",
		section = barSettingsSection,
		position = 3
	)
	default int barTextXOffset() { return 0; }

	@ConfigItem(
		keyName = "barTextYOffset",
		name = "Text Y offset",
		description = "Y Offset of the displayed text",
		section = barSettingsSection,
		position = 4
	)
	default int barTextYOffset() { return 0; }

	@ConfigItem(
		keyName = "barHoverTolerance",
		name = "Hover tolerance",
		description = "The amount of tolerance when determining if bar is being hovered",
		section = barSettingsSection,
		position = 5
	)
	default int barHoverTolerance() { return 8; }

	// endregion

	// region Enums

	@Getter
	@RequiredArgsConstructor
	enum HitpointsDisplayStyle {
		HITPOINTS("Hitpoints"),
		PERCENTAGE("Percentage"),
		BOTH("Both")
		;

		@Getter
		private final String name;

		@Override
		public String toString() { return name; }
	}

	@Getter
	@RequiredArgsConstructor
	enum DisplayBarTextOptions {
		OFF("Off"),
		HOVER("Hover"),
		ALWAYS("Always"),
		;

		@Getter
		private final String name;

		@Override
		public String toString() { return name; }
	}

	// endregion

}
