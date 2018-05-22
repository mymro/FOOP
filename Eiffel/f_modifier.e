note
	description: "F-modifier for pathfinding. Changes the f value for A*"
	author: "Constantin Budin"
	date: "22.05.2018"
	revision: "1.0"

deferred class
	F_MODIFIER

feature {ANY}

	get_modifier_at(x,y:INTEGER):REAL_64
	--returns modifier at position x,y
		deferred
		end


end
