note
	description: "Root object for the game. Conatins the labyrinth"
	author: "Constantin Budin"
	date: "18.05.2018"
	revision: "0.1"
--missing a lot of features from the java version
class
	MAIN_LABYRINTH

create
	create_new_labyrinth

feature {NONE}
	labyrinth: LABYRINTH
	dimension: VECTOR_2
	buffer_index: INTEGER
	pos: VECTOR_2
	game: GAME
	parent: detachable GAME_OBJECT


feature {NONE}

	create_new_labyrinth(a_game: GAME; a_parent: detachable GAME_OBJECT; a_dimension, a_pos:VECTOR_2; a_buffer_index:INTEGER)
		require
			a_dimension.x >=1
			a_dimension.y >=1
		do
			dimension := a_dimension
			create labyrinth.make (dimension)
			buffer_index := a_buffer_index
			pos:= a_pos
			game:= a_game
			parent:= a_parent
			labyrinth.create_labyrinth
			reset_buffer
		end

	fill_buffer(buffer: separate EV_PIXMAP_ADVANCED)
	--draws labyrinth to a buffer
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
			step_width := (buffer.width/dimension.x).truncated_to_integer
			step_width_half := (step_width/2).truncated_to_integer
			step_height := (buffer.height/dimension.y).truncated_to_integer
			step_height_half := (step_height/2).truncated_to_integer
			x_finish := 0
			y_finish:= 0
			buffer.set_line_width (step_width-2)
			buffer.set_foreground_color_rgb (0,0,0)
			buffer.fill_rectangle (0, 0, buffer.width, buffer.height)
			buffer.set_foreground_color_rgb (1, 1, 1)

			across
				1 |..| dimension.x as i
			loop
				across
					 1 |..| dimension.y as j
				loop
					current_node := labyrinth[i.item,j.item]
					center_x := (current_node.x - 1) * step_width + step_width_half
					center_y := (current_node.y - 1) * step_height + step_height_half

					if current_node.neighbours.at ("up") /= void then
						buffer.draw_segment (center_x, center_y, center_x, center_y - step_height_half)
					end
					if current_node.neighbours.at ("down") /= void then
						buffer.draw_segment (center_x, center_y, center_x, center_y + step_height_half)
					end
					if current_node.neighbours.at ("left") /= void then
						buffer.draw_segment (center_x, center_y, center_x - step_width_half, center_y)
					end
					if current_node.neighbours.at ("right") /= void then
						buffer.draw_segment (center_x, center_y, center_x + step_width_half, center_y)
					end

					if current_node.type = 0 then
						x_finish := (current_node.x-1) * step_width
						y_finish := (current_node.y-1) * step_height
					end
				end
			end

			buffer.set_foreground_color_rgb (1, 0, 0)
			if step_width < step_height then
				buffer.fill_ellipse (x_finish, y_finish, step_width, step_width)
			else
				buffer.fill_ellipse (x_finish, y_finish, step_height, step_height)
			end
		end


feature {ANY}

	reset_buffer
	-- redraws the buffer
		do
			if attached game.get_buffer (buffer_index) as buffer_pixmap then
				fill_buffer(buffer_pixmap)
			else
				print("buffer not attached in main labyrinth")
			end
		end

	draw
	-- draw to display
		do
			game.draw_buffer_to_display (buffer_index, pos)
		end
end
