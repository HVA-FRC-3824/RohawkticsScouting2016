package com.team3824.akmessing1.scoutingapp.utilities;

/**
 * Interface that contains all of the constants used through the app
 */
public interface Constants {

    String VERSION = "Version: 2.0.4";
    String APP_DATA = "appData";
    int OUR_TEAM_NUMBER = 3824;

    // Settings
    interface Settings {
        String EVENT_ID = "event_id";
        String USER_TYPE = "user_type";
        String ALLIANCE_COLOR = "alliance_color";
        String ALLIANCE_NUMBER = "alliance_number";
        String PIT_GROUP_NUMBER = "pit_group_number";
    }

    //User Types
    interface User_Types {
        String MATCH_SCOUT = "Match Scout";
        String PIT_SCOUT = "Pit Scout";
        String SUPER_SCOUT = "Super Scout";
        String DRIVE_TEAM = "Drive Team";
        String STRATEGY = "Strategy";
        String SERVER = "Server";
        String ADMIN = "Admin";
    }

    //Alliance Colors
    interface Alliance_Colors {
        String BLUE = "Blue";
        String RED = "Red";
    }

    // Intent Extras
    interface Intent_Extras {
        String TEAM_NUMBER = "team_number";
        String MATCH_NUMBER = "match_number";
        String NEXT_PAGE = "next_page";
        String MATCH_SCOUTING = "match_scouting";
        String SUPER_SCOUTING = "super_scouting";
        String MATCH_VIEWING = "match_viewing";
        String DRIVE_TEAM_FEEDBACK = "drive_team_feedback";
        String PIT_SCOUTING = "pit_scouting";
        String TEAM_VIEWING = "team_viewing";
    }

    // Auto
    interface Auto_Inputs {
        String AUTO_START_POSITION = "auto_start_position";
        String AUTO_REACH_CROSS = "auto_reach_cross";
        String AUTO_HIGH_HIT = "auto_high_hit";
        String AUTO_HIGH_MISS = "auto_high_miss";
        String AUTO_LOW_HIT = "auto_low_hit";
        String AUTO_LOW_MISS = "auto_low_miss";
    }

    // Teleop
    interface Teleop_Inputs {
        // Defenses
        String TELEOP_DEFENSE_1 = "teleop_defense_1";
        String TELEOP_DEFENSE_2 = "teleop_defense_2";
        String TELEOP_DEFENSE_3 = "teleop_defense_3";
        String TELEOP_DEFENSE_4 = "teleop_defense_4";
        String TELEOP_DEFENSE_5 = "teleop_defense_5";

        // These two arrays must have the same length
        String[] TELEOP_DEFENSE_TIMES = {"< 5s", "< 10s", "> 10s", "Stuck"};
        int[] TELEOP_DEFENSE_TIMES_VALUE = {5, 10, 15, 30};

        // Shooting
        String SELECT_AIM_TIME = "Select Aim Time";
        String[] TELEOP_AIM_TIMES = {SELECT_AIM_TIME, "< 2s", "< 5s", "< 8s", "< 10s", "< 15s", "> 15s"};
        int[] TELEOP_AIM_TIMES_VALUE = {2, 5, 8, 10, 15, 20};

        String SELECT_SHOT_POSITION = "Select Position";
        String OUTER_WORKS = "Outer Works";
        String ON_NEAR_CENTER_BATTER = "On/Near Center Batter";
        String ALIGNMENT_LINE = "Alignment Line";
        String[] TELEOP_SHOT_POSITIONS = {SELECT_SHOT_POSITION, OUTER_WORKS, "On/Near Left Batter", ON_NEAR_CENTER_BATTER, "On/Near Right Batter", ALIGNMENT_LINE, "Open Space"};

        String TELEOP_HIGH_SHOT = "teleop_high_shot";
        String TELEOP_LOW_SHOT = "teleop_low_shot";

        String SHOT_POSITION = "shot_position";
        String AIM_TIME = "aim_time";
        String SHOT_HIT_MISS = "shot_hit_miss";
    }

    // Endgame
    interface Endgame_Inputs {
        String ENDGAME_CHALLENGE_SCALE = "endgame_challenge_scale";

        int ENDGAME_NOTHING = 0;
        int ENDGAME_FAILED_CHALLENGE = 1;
        int ENDGAME_CHALLENGE = 2;
        int ENDGAME_FAILED_SCALE = 3;
        int ENDGAME_SCALE = 4;

        String[] ENDGAME_OPTIONS = {"Nothing", "Failed Challenge", "Challenge", "Failed Scale", "Scale"};

    }

    // Post Match
    interface Post_Match_Inputs {
        String POST_DQ = "post_dq";
        String POST_STOPPED = "post_stopped";
        String POST_DIDNT_SHOW_UP = "post_didnt_show_up";
        String POST_NOTES = "post_notes";
    }

    // Foul
    interface Foul_Inputs {
        String FOUL_STANDARD = "foul_standard";
        String FOUL_TECH = "foul_tech";
        String FOUL_YELLOW_CARD = "foul_yellow_card";
        String FOUL_RED_CARD = "foul_red_card";
    }

    // Pit
    interface Pit_Inputs {
        String PIT_ROBOT_PICTURE = "pit_robot_picture";
        String PIT_ROBOT_WIDTH = "pit_robot_width";
        String PIT_ROBOT_LENGTH = "pit_robot_length";
        String PIT_ROBOT_HEIGHT = "pit_robot_height";
        String PIT_ROBOT_WEIGHT = "pit_robot_weight";
        String PIT_DRIVETRAIN = "pit_drivetrain";
        String PIT_NUMBER_OF_CIMS = "pit_number_cims";
        String PIT_PROGRAMMING_LANGUAGE = "pit_programming_language";
        String PIT_NOTES = "pit_notes";
    }


    // Super
    interface Super_Inputs {
        String SUPER_NOTES = "super_notes";

        String RED_DEFENSE_2 = "red_defense2";
        String RED_DEFENSE_4 = "red_defense4";
        String RED_DEFENSE_5 = "red_defense5";

        String BLUE_DEFENSE_2 = "blue_defense2";
        String BLUE_DEFENSE_4 = "blue_defense4";
        String BLUE_DEFENSE_5 = "blue_defense5";

        String DEFENSE_3 = "defense3";

        String[] SUPER_DEFENSES = {BLUE_DEFENSE_2, BLUE_DEFENSE_4, BLUE_DEFENSE_5,
                RED_DEFENSE_2, RED_DEFENSE_4, RED_DEFENSE_5, DEFENSE_3};
    }

    // Defense Arrays
    interface Defense_Arrays {
        String[] DEFENSES = {"low_bar", "portcullis", "cheval_de_frise", "moat", "ramparts",
                "drawbridge", "sally_port", "rock_wall", "rough_terrain"};
        String[] DEFENSES_ABREV = {"LB", "P", "CdF", "M", "R", "D", "SP", "RW", "RT"};
        String[] DEFENSES_LABEL = {"Low Bar", "Portcullis", "Cheval de Frise", "Moat",
                "Ramparts", "Drawbridge", "Sally Port", "Rock Wall", "Rough Terrain"};

        int LOW_BAR_INDEX = 0;
        int PORTCULLIS_INDEX = 1;
        int CHEVAL_DE_FRISE_INDEX = 2;
        int MOAT_INDEX = 3;
        int RAMPARTS_INDEX = 4;
        int DRAWBRIDGE_INDEX = 5;
        int SALLY_PORT_INDEX = 6;
        int ROCK_WALL_INDEX = 7;
        int ROUGH_TERRAIN_INDEX = 8;
    }

    // Totals
    interface Calculated_Totals {
        String[] TOTAL_DEFENSES_SEEN = {"total_seen_low_bar", "total_seen_portcullis",
                "total_seen_cheval_de_frise", "total_seen_moat", "total_seen_ramparts", "total_seen_drawbridge",
                "total_seen_sally_port", "total_seen_rock_wall", "total_seen_rough_terrain"};
        String[] TOTAL_DEFENSES_STARTED = {"total_start_low_bar", "total_start_portcullis",
                "total_start_cheval_de_frise", "total_start_moat", "total_start_ramparts", "total_start_drawbridge",
                "total_start_sally_port", "total_start_rock_wall", "total_start_rough_terrain", "total_start_spybox", "total_start_secret_passage"};
        String[] TOTAL_DEFENSES_AUTO_REACHED = {"total_auto_low_bar_reach", "total_auto_portcullis_reach",
                "total_auto_cheval_de_frise_reach", "total_auto_moat_reach", "total_auto_ramparts_reach", "total_auto_drawbridge_reach",
                "total_auto_sally_port_reach", "total_auto_rock_wall_reach", "total_auto_rough_terrain_reach"};
        String[] TOTAL_DEFENSES_AUTO_CROSSED = {"total_auto_low_bar_cross", "total_auto_portcullis_cross",
                "total_auto_cheval_de_frise_cross", "total_auto_moat_cross", "total_auto_ramparts_cross", "total_auto_drawbridge_cross",
                "total_auto_sally_port_cross", "total_auto_rock_wall_cross", "total_auto_rough_terrain_cross"};
        String[] TOTAL_DEFENSES_TELEOP_CROSSED = {"total_teleop_low_bar", "total_teleop_portcullis",
                "total_teleop_cheval_de_frise", "total_teleop_moat", "total_teleop_ramparts", "total_teleop_drawbridge",
                "total_teleop_sally_port", "total_teleop_rock_wall", "total_teleop_rough_terrain"};
        String[] TOTAL_DEFENSES_TELEOP_NOT_CROSSED = {"total_teleop_not_low_bar", "total_teleop_not_portcullis",
                "total_teleop_not_cheval_de_frise", "total_teleop_not_moat", "total_teleop_not_ramparts", "total_teleop_not_drawbridge",
                "total_teleop_not_sally_port", "total_teleop_not_rock_wall", "total_teleop_not_rough_terrain"};
        String[] TOTAL_DEFENSES_TELEOP_CROSSED_POINTS = {"total_teleop_points_low_bar", "total_teleop_points_portcullis",
                "total_teleop_points_cheval_de_frise", "total_teleop_points_moat", "total_teleop_points_ramparts", "total_teleop_points_drawbridge",
                "total_teleop_points_sally_port", "total_teleop_points_rock_wall", "total_teleop_points_rough_terrain"};
        String[] TOTAL_DEFENSES_TELEOP_TIME = {"total_teleop_low_bar_time", "total_teleop_portcullis_time",
                "total_teleop_cheval_de_frise_time", "total_teleop_moat_time", "total_teleop_ramparts_time", "total_teleop_drawbridge_time",
                "total_teleop_sally_port_time", "total_teleop_rock_wall_time", "total_teleop_rough_terrain_time"};

        String TOTAL_AUTO_HIGH_HIT = "total_auto_high_hit";
        String TOTAL_AUTO_HIGH_MISS = "total_auto_high_miss";
        String TOTAL_AUTO_LOW_HIT = "total_auto_low_hit";
        String TOTAL_AUTO_LOW_MISS = "total_auto_low_miss";

        String TOTAL_TELEOP_HIGH_HIT = "total_teleop_high_hit";
        String TOTAL_TELEOP_HIGH_MISS = "total_teleop_high_miss";
        String[] TOTAL_TELEOP_HIGH_POSITIONS = {"total_teleop_high_outer_works", "total_teleop_high_left_batter", "total_teleop_high_center_batter", "total_teleop_high_right_batter", "total_teleop_high_alignment_line", "total_teleop_high_open_space"};
        String[] TOTAL_TELEOP_HIGH_POSITIONS_HIT = {"total_teleop_high_outer_works_hit", "total_teleop_high_left_batter_hit", "total_teleop_high_center_batter_hit", "total_teleop_high_right_batter_hit", "total_teleop_high_alignment_line_hit", "total_teleop_high_open_space_hit"};
        String[] TOTAL_TELEOP_HIGH_POSITIONS_MISS = {"total_teleop_high_outer_works_miss", "total_teleop_high_left_batter_miss", "total_teleop_high_center_batter_miss", "total_teleop_high_right_batter_miss", "total_teleop_high_alignment_line_miss", "total_teleop_high_open_space_miss"};
        String[] TOTAL_TELEOP_HIGH_POSITIONS_LABEL = {"Outer Works", "Left Batter", "Center Batter", "Right Batter", "Alignment Line", "Open Space"};
        String TOTAL_TELEOP_HIGH_AIM_TIME = "total_teleop_high_aim_time";

        int SHOT_POSITION_HIGH_OUTER_WORKS = 0;
        int SHOT_POSITION_HIGH_LEFT_BATTER = 1;
        int SHOT_POSITION_HIGH_CENTER_BATTER = 2;
        int SHOT_POSITION_HIGH_RIGHT_BATTER = 3;
        int SHOT_POSITION_HIGH_ALIGNMENT_LINE = 4;
        int SHOT_POSITION_HIGH_OPEN_SPACE = 5;

        String TOTAL_TELEOP_LOW_HIT = "total_teleop_low_hit";
        String TOTAL_TELEOP_LOW_MISS = "total_teleop_low_miss";
        String[] TOTAL_TELEOP_LOW_POSITIONS = {"total_teleop_low_left_batter", "total_teleop_low_right_batter", "total_teleop_low_open_space"};
        String[] TOTAL_TELEOP_LOW_POSITIONS_HIT = {"total_teleop_low_left_batter_hit", "total_teleop_low_right_batter_hit", "total_teleop_low_open_space_hit"};
        String[] TOTAL_TELEOP_LOW_POSITIONS_MISS = {"total_teleop_low_left_batter_miss", "total_teleop_low_right_batter_miss", "total_teleop_low_open_space_miss"};
        String[] TOTAL_TELEOP_LOW_POSITIONS_LABEL = {"Left Batter", "Right Batter", "Open Space"};
        String TOTAL_TELEOP_LOW_AIM_TIME = "total_teleop_low_aim_time";

        int SHOT_POSITION_LOW_LEFT_BATTER = 0;
        int SHOT_POSITION_LOW_RIGHT_BATTER = 1;
        int SHOT_POSITION_LOW_OPEN_SPACE = 2;

        String TOTAL_FAILED_CHALLENGE = "total_failed_challenge";
        String TOTAL_CHALLENGE = "total_challenge";
        String TOTAL_FAILED_SCALE = "total_failed_scale";
        String TOTAL_SCALE = "total_scale";

        String TOTAL_DQ = "total_dq";
        String TOTAL_STOPPED = "total_stopped";
        String TOTAL_DIDNT_SHOW_UP = "total_didnt_show_up";

        String TOTAL_FOULS = "total_fouls";
        String TOTAL_TECH_FOULS = "total_tech_fouls";
        String TOTAL_YELLOW_CARDS = "total_yellow_cards";
        String TOTAL_RED_CARDS = "total_red_cards";

        String TOTAL_MATCHES = "total_matches";
    }

    // Pick List
    interface Pick_List {
        String SHOOTER_PICKABILITY = "shooter_pickability";
        String BREACHER_PICKABILITY = "breacher_pickability";
        String OFFENSIVE_PICKABILITY = "offensive_pickability";
        String DEFENSIVE_PICKABILITY = "defensive_pickability";

        String BOTTOM_TEXT = "bottom_text";

        String PICK_RANK = "_pick_rank";
        String PICKABILITY = "_pickability";
    }

    // Match Schedule;
    interface Match_Schedule {
        int BLUE1_INDEX = 0;
        int BLUE2_INDEX = 1;
        int BLUE3_INDEX = 2;
        int RED1_INDEX = 3;
        int RED2_INDEX = 4;
        int RED3_INDEX = 5;
    }

    //Bluetooth
    interface Bluetooth {
        int CONNECTION_TIMEOUT = 5000;
        int NUM_ATTEMPTS = 7;
        int CHUNK_SIZE = 4192;
        int HEADER_MSB = 0x10;
        int HEADER_LSB = 0x55;

        String SERVER_NAME = "3824_Server";

        char SUPER_HEADER = 'S';
        char MATCH_HEADER = 'M';
        char PIT_HEADER = 'P';
        char DRIVE_TEAM_FEEDBACK_HEADER = 'D';
        char STATS_HEADER = 'A';
        char FILENAME_HEADER = 'F';
        char FILE_HEADER = 'f';
        char SCHEDULE_HEADER = 's';
        char REQUEST_HEADER = 'R';
        char RECEIVE_HEADER = 'r';
        char PING_HEADER = 'p';

        String REQUEST_MATCH = String.format("%c%c", REQUEST_HEADER, MATCH_HEADER);
        String REQUEST_SUPER = String.format("%c%c", REQUEST_HEADER, SUPER_HEADER);
        String REQUEST_PIT = String.format("%c%c", REQUEST_HEADER, PIT_HEADER);
        String REQUEST_STATS = String.format("%c%c", REQUEST_HEADER, STATS_HEADER);
        String REQUEST_DRIVE_TEAM_FEEDBACK = String.format("%c%c", REQUEST_HEADER, DRIVE_TEAM_FEEDBACK_HEADER);
        String REQUEST_SCHEDULE = String.format("%c%c", REQUEST_HEADER, SCHEDULE_HEADER);

        String PING = "ping";
        String PONG = "pong";
    }

    //MessageType
    interface Message_Type {
        int DATA_SENT_OK = 0x00;
        int DATA_RECEIVED = 0x02;
        int SENDING_DATA = 0x04;

        int DIGEST_DID_NOT_MATCH = 0x50;
        int COULD_NOT_CONNECT = 0x51;
        int INVALID_HEADER = 0x52;
        int CONNECTION_LOST = 0x53;
    }

    // Sync Activity
    interface Sync_Activity {
        String SYNC_NONE = "None";
        int SYNC_NONE_INDEX = 0;

        String SYNC_PING = "Ping";
        int SYNC_PING_INDEX = 1;

        String SYNC_SEND_MATCH = "Send Match Data";
        int SYNC_SEND_MATCH_INDEX = 2;

        String SYNC_SEND_PIT = "Send Pit Data";
        int SYNC_SEND_PIT_INDEX = 3;

        String SYNC_SEND_SUPER = "Send Super Data";
        int SYNC_SEND_SUPER_INDEX = 4;

        String SYNC_SEND_FEEDBACK = "Send Drive Team Feedback";
        int SYNC_SEND_FEEDBACK_INDEX = 5;

        String SYNC_SEND_STATS = "Send Stats";
        int SYNC_SEND_STATS_INDEX = 6;

        String SYNC_SEND_ALL = "Send All";
        int SYNC_SEND_ALL_INDEX = 7;

        String SYNC_SEND_SCHEDULE = "Send Schedule";
        int SYNC_SEND_SCHEDULE_INDEX = 8;

        String SYNC_RECEIVE_MATCH = "Receive Match Data";
        int SYNC_RECEIVE_MATCH_INDEX = 9;

        String SYNC_RECEIVE_PIT = "Receive Pit Data";
        int SYNC_RECEIVE_PIT_INDEX = 10;

        String SYNC_RECEIVE_SUPER = "Receive Super Data";
        int SYNC_RECEIVE_SUPER_INDEX = 11;

        String SYNC_RECEIVE_FEEDBACK = "Receive Drive Team Feedback";
        int SYNC_RECEIVE_FEEDBACK_INDEX = 12;

        String SYNC_RECEIVE_STATS = "Receive Stats";
        int SYNC_RECEIVE_STATS_INDEX = 13;

        String SYNC_RECEIVE_ALL = "Receive All";
        int SYNC_RECEIVE_ALL_INDEX = 14;

        String SYNC_RECEIVE_SCHEDULE = "Receive Schedule";
        int SYNC_RECEIVE_SCHEDULE_INDEX = 15;

        String[] SYNC_ACTIONS = {SYNC_NONE, SYNC_PING, SYNC_SEND_MATCH, SYNC_SEND_PIT,
                SYNC_SEND_SUPER, SYNC_SEND_FEEDBACK, SYNC_SEND_STATS, SYNC_SEND_ALL, SYNC_SEND_SCHEDULE,
                SYNC_RECEIVE_MATCH, SYNC_RECEIVE_PIT, SYNC_RECEIVE_SUPER, SYNC_RECEIVE_FEEDBACK, SYNC_RECEIVE_STATS,
                SYNC_RECEIVE_ALL, SYNC_RECEIVE_SCHEDULE};
    }

    //Alliance Selection
    interface Alliance_Selection {
        int ALLIANCE_1_INDEX = 0;
        int ALLIANCE_2_INDEX = 1;
        int ALLIANCE_3_INDEX = 2;
        int ALLIANCE_4_INDEX = 3;
        int ALLIANCE_5_INDEX = 4;
        int ALLIANCE_6_INDEX = 5;
        int ALLIANCE_7_INDEX = 6;
        int ALLIANCE_8_INDEX = 7;

        int CAPTAIN_OFFSET = 0;
        int FIRST_PICK_OFFSET = 8;
        int SECOND_PICK_OFFSET = 16;

        String MATCH_TYPE = "match_type";
        String BLUE1 = "blue1";
        String BLUE2 = "blue2";
        String BLUE3 = "blue3";
        String RED1 = "red1";
        String RED2 = "red2";
        String RED3 = "red3";

        String SELECT_TEAM = "Select Team";
    }

    //Event View
    interface EVENT_VIEW {
        String[] EVENT_VIEW_DROPDOWN = {"Points", "Defenses", "High Goal", "Low Goal",
                "Auto", "Endgame", "Fouls"};

        int POINTS = 0;
        int DEFENSES = 1;
        int HIGH_GOAL = 2;
        int LOW_GOAL = 3;
        int AUTO = 4;
        int ENDGAME = 5;
        int FOULS = 6;
    }
}
