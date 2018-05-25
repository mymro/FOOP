note
	description: "Key bindings for player 1"
	author: "Constantin Budin"
	date: "25.05.2018"
	revision: "1.0"

class
	PLAYER_1_KEY_BINDINGS

inherit
	KEY_BINDINGS

feature{ANY}
	up_code: INTEGER = 90 -- W
	down_code: INTEGER = 86 -- S
	left_code: INTEGER = 68 -- A
	right_code: INTEGER = 71 -- D
	place_repell_flag_code: INTEGER = 84 -- Q
	place_attract_flag_code: INTEGER = 72 -- E
end
