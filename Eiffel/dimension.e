note
	description: "essentialla a 2 dimensional named vector"
	author: "Constantin Budin"
	date: "18.05.2018"
	revision: "0.1"

class
	DIMENSION
inherit
	ANY
		redefine
			default_create
		end

create
	default_create,
	make_with_dimensions

feature{ANY}
	width: INTEGER
	height: INTEGER

feature {NONE}
	default_create
	--creates dimension with 1,1
		do
			width:=1
			height:=1
		ensure then
			width = 1
			height = 1
		end

	make_with_dimensions(a_width, a_height:INTEGER)
		require
			a_width >=1
			a_height >=1
		do
			width := a_width
			height := a_height
		ensure
			width = a_width
			height = a_height
		end

end
