package sheridan.gcaa.items.attachments.handguard.data;

import sheridan.gcaa.attachmentSys.AttachmentSlot;

import java.util.Set;

public class ARHandguardSlots {
    public static final AttachmentSlot SHORT_HANDGUARD_ROOT = AttachmentSlot.root()
            .addChild(new AttachmentSlot("handguard_grip", Set.of(
                    "gcaa:vertical_grip",
                    "gcaa:gp_25",
                    "gcaa:m203",
                    "gcaa:laser_sight",
                    "gcaa:rail_panel",
                    "gcaa:flashlight",
                    "gcaa:slant_grip",
                    "gcaa:rail_panel_short")).lower())
            .addChild(new AttachmentSlot("handguard_scope", Set.of(
                    "gcaa:red_dot",
                    "gcaa:holographic",
                    "gcaa:acog",
                    "gcaa:okp7_b",
                    "gcaa:flashlight",
                    "gcaa:horizontal_laser_sight",
                    "gcaa:rail_panel",
                    "gcaa:rail_panel_short")).upper())
            .addChild(new AttachmentSlot("handguard_left", Set.of("" +
                    "gcaa:horizontal_laser_sight",
                    "gcaa:flashlight",
                    "gcaa:laser_sight",
                    "gcaa:rail_panel",
                    "gcaa:rail_panel_short")).lower())
            .addChild(new AttachmentSlot("handguard_right", Set.of(
                    "gcaa:horizontal_laser_sight",
                    "gcaa:flashlight",
                    "gcaa:laser_sight",
                    "gcaa:rail_panel",
                    "gcaa:rail_panel_short")).lower());

    public static final AttachmentSlot HANDGUARD_ROOT = AttachmentSlot.root()
            .addChild(new AttachmentSlot("handguard_grip", Set.of(
                    "gcaa:vertical_grip",
                    "gcaa:gp_25",
                    "gcaa:m203",
                    "gcaa:laser_sight",
                    "gcaa:rail_panel",
                    "gcaa:flashlight",
                    "gcaa:slant_grip",
                    "gcaa:rail_panel_short")).lower())
            .addChild(new AttachmentSlot("handguard_scope", Set.of(
                    "gcaa:red_dot",
                    "gcaa:holographic",
                    "gcaa:acog",
                    "gcaa:horizontal_laser_sight",
                    "gcaa:rail_panel",
                    "gcaa:okp7_b",
                    "gcaa:rail_panel_short")).upper())
            .addChild(new AttachmentSlot("handguard_left", Set.of(
                    "gcaa:horizontal_laser_sight",
                    "gcaa:flashlight",
                    "gcaa:laser_sight",
                    "gcaa:rail_panel",
                    "gcaa:rail_panel_short")).lower())
            .addChild(new AttachmentSlot("handguard_right", Set.of(
                    "gcaa:horizontal_laser_sight",
                    "gcaa:flashlight",
                    "gcaa:laser_sight",
                    "gcaa:rail_panel",
                    "gcaa:rail_panel_short")).lower())
            .addChild(new AttachmentSlot("handguard_left_front", Set.of(
                    "gcaa:laser_sight",
                    "gcaa:horizontal_laser_sight",
                    "gcaa:rail_panel",
                    "gcaa:rail_panel_short",
                    "gcaa:flashlight")).lower())
            .addChild(new AttachmentSlot("handguard_right_front", Set.of(
                    "gcaa:laser_sight",
                    "gcaa:horizontal_laser_sight",
                    "gcaa:rail_panel",
                    "gcaa:rail_panel_short",
                    "gcaa:flashlight")).lower())
            .addChild(new AttachmentSlot("handguard_front", Set.of(
                    "gcaa:laser_sight",
                    "gcaa:horizontal_laser_sight",
                    "gcaa:flashlight",
                    "gcaa:rail_panel_short")).lower());
}
