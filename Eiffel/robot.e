note
	description: "Robot class. "
	author: "Constantin Budin"
	date: "23.05.2018"
	revision: "1.0"

class
	ROBOT

inherit
	GAME_OBJECT
		redefine
			draw,
			update,
			fill_buffer_using_mask
		end

create
	make_robot

feature {ANY}
	current_delta_time: REAL_64
	current_path_index: INTEGER
	node_type_helper: NODE_TYPE_BASE
	seconds_per_field: REAL_64 = 0.4
	name: STRING

feature {NONE}
	pos_in_labyrinth: VECTOR_2
	main_labyrinth: MAIN_LABYRINTH
	personal_labyrinth: LABYRINTH
	f_modifier: F_MODIFIER_CONTAINER
	current_node: LABYRINTH_NODE
	next_node: LABYRINTH_NODE
	current_path: ARRAYED_LIST[LABYRINTH_NODE]
	color: EV_COLOR


feature {NONE}
	make_robot(a_game: GAME; a_labyrinth:MAIN_LABYRINTH; a_pos_in_labyrinth: VECTOR_2; a_color: EV_COLOR; a_name: STRING)
		require
			a_pos_in_labyrinth.x > 0 and a_pos_in_labyrinth.x <= a_labyrinth.get_labyrinth_dim_x
			a_pos_in_labyrinth.y > 0 and a_pos_in_labyrinth.y <= a_labyrinth.get_labyrinth_dim_y
		local
			a_pos: VECTOR_2
			a_dimension: VECTOR_2
			a_margin: VECTOR_2
		do
			main_labyrinth:=a_labyrinth
			create personal_labyrinth.make(create {VECTOR_2}.make_with_xy(main_labyrinth.get_labyrinth_dim_x, main_labyrinth.get_labyrinth_dim_y))
			create f_modifier
			pos_in_labyrinth:=a_pos_in_labyrinth
			current_delta_time:= 0
			current_path_index:= 1
			color:= a_color
			name:= a_name
			create current_path.make (0)
			create node_type_helper
			current_node:= personal_labyrinth[pos_in_labyrinth.x, pos_in_labyrinth.y]
			next_node:= personal_labyrinth[pos_in_labyrinth.x, pos_in_labyrinth.y]
			copy_node_at(pos_in_labyrinth.x, pos_in_labyrinth.y)

			create a_pos.make_with_xy ((pos_in_labyrinth.x-1)*main_labyrinth.step_width, (pos_in_labyrinth.y-1)*main_labyrinth.step_height)
			create a_dimension.make_with_xy (main_labyrinth.step_width, main_labyrinth.step_height)
			create a_margin.make_with_xy (3, 3)
			make(a_game, a_pos, a_dimension, a_margin, 1, 2)
			reset_buffer
		end

	copy_node_at(i,j:INTEGER)
	-- copies all the features of a node in main_labyrinth to corresponding node in private_labyrinth
	-- and also copies the connections
		require
			i > 0 and i <= main_labyrinth.get_labyrinth_dim_x and i <= personal_labyrinth.get_dimension_x
			j > 0 and j <= main_labyrinth.get_labyrinth_dim_y and j <= personal_labyrinth.get_dimension_y
		local
			keys: ARRAYED_LIST[STRING]
			rand: RANDOM
			index: INTEGER
		do
			personal_labyrinth[i,j].set_type(main_labyrinth[i,j].type)

			from
				create rand.set_seed ((create{TIME}.make_now).seconds)
				create keys.make_from_array (main_labyrinth[i,j].neighbours.current_keys)
			until
				keys.is_empty
			loop
				rand.forth
				index:= (rand.item\\keys.count) + 1

				if attached main_labyrinth[i,j].neighbours.at (keys[index]) as node then
					personal_labyrinth[i,j].neighbours.put (personal_labyrinth[node.x,node.y], keys[index])
					if keys[index] = "up" then
						personal_labyrinth[node.x,node.y].neighbours.put (personal_labyrinth[i,j], "down")
					elseif keys[index] = "down" then
						personal_labyrinth[node.x,node.y].neighbours.put (personal_labyrinth[i,j], "up")
					elseif keys[index] = "left" then
						personal_labyrinth[node.x,node.y].neighbours.put (personal_labyrinth[i,j], "right")
					elseif keys[index] = "right" then
						personal_labyrinth[node.x,node.y].neighbours.put (personal_labyrinth[i,j], "left")
					end
				end

				keys.go_i_th (index)
				keys.remove
			end
		end

	fill_buffer_using_mask(buffer, mask: separate EV_PIXMAP_ADVANCED; index_buffer, index_mask:INTEGER)
	--fills the buffer and draws mask for transparency
		local
			x_center: INTEGER
			y_center: INTEGER
		do
			x_center:= (buffer.width/2).truncated_to_integer
			y_center:= (buffer.height/2).truncated_to_integer

			mask.set_foreground_color_rgb (0,0,0)
			mask.fill_rectangle (0, 0, mask.width, mask.height)
			mask.set_foreground_color_rgb (1, 1, 1)
			mask.draw_triangle (x_center, y_center, mask.height)

			buffer.set_foreground_color_rgb(color.red, color.green, color.blue)
			buffer.fill_rectangle (0, 0, buffer.width, buffer.height)
			game.set_mask (index_buffer, index_mask)
		end
feature {ANY}

	reset_buffer
	-- redraws the buffer
		do
			if attached game.get_buffer (buffer_indices[1]) as buffer and
			attached game.get_buffer (buffer_indices[2]) as mask then
				fill_buffer_using_mask(buffer, mask, buffer_indices[1], buffer_indices[2])
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

	update
	--updates robot
		do
			current_delta_time := current_delta_time + game.delta_time

			if current_path_index >= current_path.count and not current_node.is_of_type (node_type_helper.type_finish) then
				current_path:= personal_labyrinth.get_path_from_to_nearest_node_with_type (current_node, node_type_helper.type_unknown, f_modifier)
				current_path_index := 1
				if current_path.count > 1 then
					next_node:= current_path[2]
				end
			elseif current_node.is_of_type (node_type_helper.type_finish) then
				game.robot_has_found_exit (current)
			end

			if current_delta_time >= seconds_per_field and current_path_index < current_path.count then
				current_delta_time:=0
				current_path_index := current_path_index + 1
				current_node:= current_path[current_path_index]

				if current_path_index < current_path.count then
					next_node:= current_path[current_path_index+1]
				end
				pos_relative_to_parent.x:= (current_node.x - 1) * main_labyrinth.step_width
				pos_relative_to_parent.y:= (current_node.y - 1) * main_labyrinth.step_height

				if current_node.is_of_type (node_type_helper.type_unknown) then
					copy_node_at(current_node.x, current_node.y)
				end

			elseif current_node /= next_node then
				pos_relative_to_parent.x:= ((current_node.x-1)* main_labyrinth.step_width + (next_node.x - current_node.x) * main_labyrinth.step_width * current_delta_time/seconds_per_field).truncated_to_integer
				pos_relative_to_parent.y:= ((current_node.y-1)* main_labyrinth.step_height + (next_node.y - current_node.y) * main_labyrinth.step_height * current_delta_time/seconds_per_field).truncated_to_integer
			end

			PRECURSOR{GAME_OBJECT}
		end
end
