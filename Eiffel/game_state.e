note
	description: "Summary description for {GAME_STATE}."
	author: ""
	date: "$Date$"
	revision: "$Revision$"

class
	GAME_STATE

	create
		make

	feature {ANY}
		state:INTEGER assign set_state

		make
			do
				state := 1
			end

		set_state(new_state: Integer)
			do
				state:= new_state
			end

end
