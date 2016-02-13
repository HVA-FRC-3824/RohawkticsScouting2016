package com.team3824.akmessing1.scoutingapp;

public interface Constants {

    // Message types sent from the Handler
    public static final int MESSAGE_READ=0;
    public static final int MESSAGE_WRITE=1;

    // Settings
    public static final String EVENT_ID = "event_id";
    public static final String USER_TYPE = "user_type";
    public static final String ALLIANCE_COLOR = "alliance_color";
    public static final String ALLIANCE_NUMBER = "alliance_number";

    //User Types
    public static final String MATCH_SCOUT = "Match Scout";
    public static final String PIT_SCOUT = "Pit Scout";
    public static final String SUPER_SCOUT = "Super Scout";
    public static final String DRIVE_TEAM = "Drive Team";
    public static final String STRATEGY = "Strategy";
    public static final String ADMIN = "Admin";

    //Alliance Colors
    public static final String BLUE = "Blue";
    public static final String RED = "Red";

    // Auto
    public static final String AUTO_START_POSITION = "auto_start_position";
    public static final String AUTO_REACH_CROSS = "auto_reach_cross";
    public static final String AUTO_HIGH_HIT = "auto_high_hit";
    public static final String AUTO_HIGH_MISS = "auto_high_miss";
    public static final String AUTO_LOW_HIT = "auto_low_hit";
    public static final String AUTO_LOW_MISS = "auto_low_miss";

    // Teleop
    public static final String TELEOP_DEFENSE_1 = "teleop_defense_1";
    public static final String TELEOP_DEFENSE_2 = "teleop_defense_2";
    public static final String TELEOP_DEFENSE_3 = "teleop_defense_3";
    public static final String TELEOP_DEFENSE_4 = "teleop_defense_4";
    public static final String TELEOP_DEFENSE_5 = "teleop_defense_5";

    public static final String[] TELEOP_DEFENSE_TIMES = {"< 5", "< 10", "> 10","Stuck"};

    public static final String TELEOP_HIGH_HIT = "teleop_high_hit";
    public static final String TELEOP_HIGH_MISS = "teleop_high_miss";
    public static final String TELEOP_LOW_HIT = "teleop_low_hit";
    public static final String TELEOP_LOW_MISS = "teleop_low_miss";

    // Endgame
    public static final String ENDGAME_CHALLENGE_SCALE = "endgame_challenge_scale";

    // Post
    public static final String POST_DQ = "post_dq";
    public static final String POST_STOPPED = "post_stopped";
    public static final String POST_DIDNT_SHOW_UP = "post_didnt_show_up";
    public static final String POST_NOTES = "post_notes";

    // Fouls
    public static final String FOUL_STANDARD = "foul_standard";
    public static final String FOUL_TECH = "foul_tech";
    public static final String FOUL_YELLOW_CARD = "foul_yellow_card";
    public static final String FOUL_RED_CARD = "foul_red_card";

    // Pit
    public static final String PIT_ROBOT_PICTURE = "robotPicture";
    public static final String PIT_ROBOT_WIDTH = "pit_robot_width";
    public static final String PIT_ROBOT_LENGTH = "pit_robot_length";
    public static final String PIT_ROBOT_HEIGHT = "pit_robot_height";
    public static final String PIT_ROBOT_WEIGHT = "pit_robot_weight";
    public static final String PIT_NOTES = "pit_notes";
    public static final String PIT_DRIVETRAIN = "pit_drivetrain";
    public static final String PIT_NUMBER_OF_CIMS = "pit_number_cims";

    // Super
    public static final String SUPER_DEFENSE_ABILITY = "super_defense_ability";
    public static final String SUPER_DRIVE_ABILITY = "super_drive_ability";
    public static final String SUPER_NOTES = "super_notes";

    public static final String RED_DEFENSE_2 = "red_defense2";
    public static final String RED_DEFENSE_4 = "red_defense4";
    public static final String RED_DEFENSE_5 = "red_defense5";

    public static final String BLUE_DEFENSE_2 = "blue_defense2";
    public static final String BLUE_DEFENSE_4 = "blue_defense4";
    public static final String BLUE_DEFENSE_5 = "blue_defense5";

    public static final String DEFENSE_3 = "defense3";

    // Defense Arrays
    public static final String[] DEFENSES = {"low_bar","portcullis","cheval_de_frise","moat","ramparts",
            "drawbridge","sally_port","rock_wall","rough_terrain"};
    public static final String[] DEFENSES_ABREV = {"LB","P","CdF","M","R","D","SP","RW","RT"};

    public static final int LOW_BAR_INDEX = 0;
    public static final int PORTCULLIS_INDEX = 1;
    public static final int CHEVAL_DE_FRISE_INDEX = 2;
    public static final int MOAT_INDEX = 3;
    public static final int RAMPARTS_INDEX = 4;
    public static final int DRAWBRIDGE_INDEX = 5;
    public static final int SALLY_PORT_INDEX = 6;
    public static final int ROCK_WALL_INDEX = 7;
    public static final int ROUGH_TERRAIN_INDEX = 8;


    // Ability Rankings
    public static final String DRIVE_ABILITY_RANKING = "super_drive_ability_ranking";
    public static final String DEFENSE_ABILITY_RANKING = "super_defense_ability_ranking";

    // Totals

    public static final String[] TOTAL_DEFENSES_SEEN = {"total_seen_low_bar", "total_seen_portcullis",
        "total_seen_cheval_de_frise","total_seen_moat","total_seen_ramparts","total_seen_drawbridge",
        "total_seen_sally_port","total_seen_rock_wall","total_seen_rough_terrain"};
    public static final String[] TOTAL_DEFENSES_STARTED = {"total_start_low_bar", "total_start_portcullis",
            "total_start_cheval_de_frise","total_start_moat","total_start_ramparts","total_start_drawbridge",
            "total_start_sally_port","total_start_rock_wall","total_start_rough_terrain", "total_start_spybox", "total_start_secret_passage"};
    public static final String[] TOTAL_DEFENSES_AUTO_REACHED = {"total_auto_low_bar_reach", "total_auto_portcullis_reach",
            "total_auto_cheval_de_frise_reach","total_auto_moat_reach","total_auto_ramparts_reach","total_auto_drawbridge_reach",
            "total_auto_sally_port_reach","total_auto_rock_wall_reach","total_auto_rough_terrain_reach"};
    public static final String[] TOTAL_DEFENSES_AUTO_CROSSED = {"total_auto_low_bar_cross", "total_auto_portcullis_cross",
            "total_auto_cheval_de_frise_cross","total_auto_moat_cross","total_auto_ramparts_cross","total_auto_drawbridge_cross",
            "total_auto_sally_port_cross","total_auto_rock_wall_cross","total_auto_rough_terrain_cross"};
    public static final String[] TOTAL_DEFENSES_TELEOP_CROSSED = {"total_teleop_low_bar", "total_teleop_portcullis",
            "total_teleop_cheval_de_frise","total_teleop_moat","total_teleop_ramparts","total_teleop_drawbridge",
            "total_teleop_sally_port","total_teleop_rock_wall","total_teleop_rough_terrain"};
    public static final String[] TOTAL_DEFENSES_TELEOP_CROSSED_POINTS = {"total_teleop_points_low_bar", "total_teleop_points_portcullis",
            "total_teleop_points_cheval_de_frise","total_teleop_points_moat","total_teleop_points_ramparts","total_teleop_points_drawbridge",
            "total_teleop_points_sally_port","total_teleop_points_rock_wall","total_teleop_points_rough_terrain"};
    public static final String[] TOTAL_DEFENSES_TELEOP_TIME = {"total_teleop_low_bar_time", "total_teleop_portcullis_time",
            "total_teleop_cheval_de_frise_time","total_teleop_moat_time","total_teleop_ramparts_time","total_teleop_drawbridge_time",
            "total_teleop_sally_port_time","total_teleop_rock_wall_time","total_teleop_rough_terrain_time"};

    public static final String TOTAL_AUTO_HIGH_HIT = "total_auto_high_hit";
    public static final String TOTAL_AUTO_HIGH_MISS = "total_auto_high_miss";
    public static final String TOTAL_AUTO_LOW_HIT = "total_auto_low_hit";
    public static final String TOTAL_AUTO_LOW_MISS = "total_auto_low_miss";

    public static final String TOTAL_TELEOP_HIGH_HIT = "total_teleop_high_hit";
    public static final String TOTAL_TELEOP_HIGH_MISS = "total_teleop_high_miss";
    public static final String TOTAL_TELEOP_LOW_HIT = "total_teleop_low_hit";
    public static final String TOTAL_TELEOP_LOW_MISS = "total_teleop_low_miss";

    public static final String TOTAL_CHALLENGE = "total_challenge";
    public static final String TOTAL_SCALE = "total_scale";

    public static final String TOTAL_DQ = "total_dq";
    public static final String TOTAL_STOPPED = "total_stopped";
    public static final String TOTAL_DIDNT_SHOW_UP = "total_didnt_show_up";

    public static final String TOTAL_FOULS = "total_fouls";
    public static final String TOTAL_TECH_FOULS = "total_tech_fouls";
    public static final String TOTAL_YELLOW_CARDS = "total_yellow_cards";
    public static final String TOTAL_RED_CARDS = "total_red_cards";

    public static final String TOTAL_MATCHES = "total_matches";

    // Pick List
    public static final String SHOOTER_PICKABILITY = "shooter_pickability";
    public static final String BREACHER_PICKABILITY = "breacher_pickability";
    public static final String OFFENSIVE_PICKABILITY = "offensive_pickability";
    public static final String DEFENSIVE_PICKABILITY = "defensive_pickability";

    public static final String BOTTOM_TEXT = "bottom_text";

    // Match Schedule;
    public static final int BLUE1_INDEX = 0;
    public static final int BLUE2_INDEX = 1;
    public static final int BLUE3_INDEX = 2;
    public static final int RED1_INDEX = 3;
    public static final int RED2_INDEX = 4;
    public static final int RED3_INDEX = 5;
}
