note
	description: "Root object for the game. Conatins the labyrinth"
	author: "Constantin Budin"
	date: "18.05.2018"
	revision: "0.1"
--sloppy implementation missing a lot of features from the java version. Just for testing.
--Has to be redone completely
class
	MAIN_LABYRINTH

inherit
	GAME_OBJECT
		redefine
			draw,
			make
		end

create
	make,
	make_with_dimension

feature {NONE}
	labyrinth: LABYRINTH
	dimension: DIMENSION

feature {NONE}
	make(x,y,a_layer:INTEGER)
		do
			create dimension.default_create
			create labyrinth.make (dimension)
			labyrinth.create_labyrinth
			PRECURSOR(x,y,a_layer)
		end
	make_with_dimension(a_dimension:DIMENSION)
		do
			dimension := a_dimension
			create labyrinth.make (dimension)
			labyrinth.create_labyrinth
			initialize(1,1,1)
		end

feature {ANY}
	draw(display: separate EV_PIXMAP_ADVANCED)-- already the same as java version just cant draw children yet
	-- draws the object and children
		local
			step_width: INTEGER_32
			step_width_half: INTEGER_32
			step_height: INTEGER_32
			step_height_half: INTEGER_32
			x_finish: INTEGER_32
			y_finish: INTEGER_32
			current_node: LABYRINTH_NODE
			center_x: INTEGER_32
			center_y: INTEGER_32
		do
			step_width := (display.width/dimension.width).truncated_to_integer
			step_width_half := (step_width/2).truncated_to_integer
			step_height := (display.height/dimension.height).truncated_to_integer
			step_height_half := (step_height/2).truncated_to_integer
			x_finish := 0
			y_finish:= 0
			display.set_line_width (step_width-2)
			display.set_foreground_color_rgb (1, 1, 1)

			across
				1 |..| dimension.width as i
			loop
				across
					 1 |..| dimension.height as j
				loop
					current_node := labyrinth[i.item,j.item]
					center_x := (current_node.x - 1) * step_width + step_width_half
					center_y := (current_node.y - 1) * step_height + step_height_half

					if current_node.neighbours.at ("up") /= void then
						display.draw_line (center_x, center_y, center_x, center_y - step_height_half)
					end
					if current_node.neighbours.at ("down") /= void then
						display.draw_line (center_x, center_y, center_x, center_y + step_height_half)
					end
					if current_node.neighbours.at ("left") /= void then
						display.draw_line (center_x, center_y, center_x - step_width_half, center_y)
					end
					if current_node.neighbours.at ("right") /= void then
						display.draw_line (center_x, center_y, center_x + step_width_half, center_y)
					end

					if current_node.type = 0 then
						x_finish := (current_node.x-1) * step_width
						y_finish := (current_node.y-1) * step_height
					end
				end
			end

			display.set_foreground_color_rgb (1, 0, 0)
			if step_width < step_height then
				display.fill_ellipse (x_finish, y_finish, step_width, step_width)
			else
				display.fill_ellipse (x_finish, y_finish, step_height, step_height)
			end

			PRECURSOR(display)
		end
end
