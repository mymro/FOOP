note
	description: "Root object for the game. Conatins the labyrinth"
	author: "Constantin Budin"
	date: "18.05.2018"
	revision: "0.1"
--missing a lot of features from the java version
class
	MAIN_LABYRINTH
inherit
	GAME_OBJECT
		redefine
			draw
		end

create
	create_new_labyrinth

feature {NONE}
	labyrinth: LABYRINTH
	labyrinth_dimension: VECTOR_2


feature {NONE}

	create_new_labyrinth(a_game: GAME; a_labyrinth_dimension, a_pos: VECTOR_2; a_layer:INTEGER; some_buffer_indices: ARRAY[INTEGER])
		require
			a_labyrinth_dimension.x >=1
			a_labyrinth_dimension.y >=1
			some_buffer_indices.count>=1
		do
			make(a_game, a_pos, a_layer, some_buffer_indices)
			labyrinth_dimension := a_labyrinth_dimension
			create labyrinth.make (labyrinth_dimension)
			labyrinth.create_labyrinth
			reset_buffer
		end

	fill_buffer(buffer: separate EV_PIXMAP_ADVANCED)
	--draws labyrinth to a buffer
		require
			(buffer.width/labyrinth_dimension.x).truncated_to_integer > 2
			(buffer.height/labyrinth_dimension.y).truncated_to_integer > 2
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
			step_width := (buffer.width/labyrinth_dimension.x).truncated_to_integer
			step_width_half := (step_width/2).truncated_to_integer
			step_height := (buffer.height/labyrinth_dimension.y).truncated_to_integer
			step_height_half := (step_height/2).truncated_to_integer
			x_finish := 0
			y_finish:= 0
			if step_width < step_height then
				buffer.set_line_width (step_width-2)
			else
				buffer.set_line_width (step_height-2)
			end
			buffer.set_foreground_color_rgb (0,0,0)
			buffer.fill_rectangle (0, 0, buffer.width, buffer.height)
			buffer.set_foreground_color_rgb (1, 1, 1)

			across
				1 |..| labyrinth_dimension.x as i
			loop
				across
					 1 |..| labyrinth_dimension.y as j
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
						if step_width < step_height then
							y_finish := y_finish + step_height_half - step_width_half
						elseif step_width > step_height then
							x_finish := x_finish + step_width_half - step_height_half
						end
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
			if attached game.get_buffer (buffer_indices[1]) as buffer_pixmap then
				fill_buffer(buffer_pixmap)
			else
				print("buffer not attached in main labyrinth")
			end
		end

	draw
	-- draw to display
		do
			draw_buffer_at_index(1)
			PRECURSOR{GAME_OBJECT}
		end
end
