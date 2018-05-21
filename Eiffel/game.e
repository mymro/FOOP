note
	description: "Main game Loop and communication with GUI"
	author: "Constantin Budin"
	date: "17.05.2018"
	revision: "0.1"

class
	GAME
	inherit
    	EXECUTION_ENVIRONMENT
    	rename
    		launch as ex_launch
    	end
create
	make

feature{NONE}
	rand: RANDOM
	game_root_objects: ARRAYED_LIST[GAME_OBJECT]
	main_window: detachable separate MAIN_WINDOW
	fps_limit: INTEGER = 60
	labyrinth_node_size: INTEGER = 20

feature{ANY}
	time_diff: REAL_64
	labyrinth_nodes_x: INTEGER
	labyrinth_nodes_y: INTEGER
	draw_area_height: INTEGER
	draw_area_width: INTEGER
	game_state: INTEGER
		--0 game shut down
		--1 checking everything is fine for game start
		--2 game running

feature {NONE}

	make
	-- eeding random number generator
	-- and resetting state
		local
			time:TIME
		do
			create time.make_now
			create rand.set_seed (time.seconds)
			game_state := 0
			draw_area_height:= 0
			draw_area_width:=0
			labyrinth_nodes_x:= 0
			labyrinth_nodes_y:= 0
			time_diff:=0
			create game_root_objects.make (0)
		end

	get_buffer_i(i:INTEGER; window: separate MAIN_WINDOW): detachable separate EV_PIXMAP_ADVANCED
	--implementation of get_buffer
		do
			RESULT:=window.get_pixmap_buffer(i)
		end

	draw_buffer_to_display_i(i: INTEGER; pos_x, pos_y:INTEGER; window: separate MAIN_WINDOW)
	--implementation of draw_buffer_to_display
		do
			window.draw_buffer_to_display (i, pos_x, pos_y)
		end

	set_mask_i(index_target, index_mask:INTEGER; window: separate MAIN_WINDOW)
		do
			window.set_mask (index_target, index_mask)
		end

feature {ANY}

	add_root(root:GAME_OBJECT)
	-- adds root object
		require
			root.game = current
		do
			if game_root_objects.count = 0 then
				game_root_objects.extend (root)
			elseif game_root_objects.occurrences (root) = 0 then
				from
					game_root_objects.start
				until
					game_root_objects.item_for_iteration.layer > root.layer or game_root_objects.islast
				loop
					game_root_objects.forth
				end

				if game_root_objects.item.layer > root.layer then
					game_root_objects.put_left (root)
				else
					game_root_objects.put_right (root)
				end
			end
		ensure
			game_root_objects.occurrences (root) = 1
		end

	get_buffer(index:INTEGER): detachable separate EV_PIXMAP_ADVANCED
	-- returns the requested buffer. If it doesnt exist void
		do
			if attached main_window as window then
				RESULT:= get_buffer_i(index, window)
			else
				print("main_window not attached in game")
				RESULT:= void
			end
		end

	draw_buffer_to_display(i: INTEGER; pos:VECTOR_2)
	-- draws a buffer to display with top left corner position of pos
		do
			if attached main_window as window then
				draw_buffer_to_display_i(i, pos.x, pos.y, window)
			else
				print("main_window not attached in game draw_bufer_to_display")
			end
		end

	set_mask(index_target, index_mask:INTEGER)
		do
			if attached main_window as window then
				set_mask_i(index_target, index_mask, window)
			else
				print("main_window not attached in game set_mask")
			end
		end

	shut_down
	-- ends the game
		do
			game_state := 0
		end

	set_up(window: separate MAIN_WINDOW; drawing_area_width, drawing_area_height:INTEGER)
	--attaches nedded objects for game
	--creates all game objects
	-- prepares for launch
	require
		window /= void
		game_state = 0
	local
		buffer_indices: ARRAY[INTEGER]
		flag: FLAG
		root: MAIN_LABYRINTH
		robot: ROBOT
	do
		main_window:=window
		draw_area_height:= drawing_area_height
		draw_area_width:= drawing_area_width
		labyrinth_nodes_x:= (draw_area_width/labyrinth_node_size).truncated_to_integer
		labyrinth_nodes_y:= (draw_area_height/labyrinth_node_size).truncated_to_integer

		-- game_objects
		create buffer_indices.make_filled (0, 1, 1)
		buffer_indices[1]:= window.create_pixmap_buffer(drawing_area_width, drawing_area_height)
		create root.create_new_labyrinth(Current, create{VECTOR_2}.make_with_xy (labyrinth_nodes_x, labyrinth_nodes_y), create{VECTOR_2}.make_with_xy (0, 0), 0, buffer_indices)
		add_root(root)

		create buffer_indices.make_filled (0, 1, 1)
		buffer_indices[1]:= window.create_pixmap_buffer_from_image ("images\missing_image.png")
		create flag.make (current, create{VECTOR_2}.make_with_xy (1000, 0), 0, buffer_indices)
		root.add_child (flag)

		create buffer_indices.make_filled (0, 1, 2)
		buffer_indices[1]:= window.create_pixmap_buffer(labyrinth_node_size, labyrinth_node_size)
		buffer_indices[2]:= window.create_pixmap_buffer(labyrinth_node_size, labyrinth_node_size)
		create robot.make_robot (current, create{VECTOR_2}.make_with_xy (500, 500), -1, buffer_indices)
		root.add_child (robot)
	end

	launch-- TODO needs frame limiter
	--launches the game
	-- the main game loop
		require
			game_state = 0
		local
			time: TIME
			last_time: REAL_64
			current_time: REAL_64
			frame_time: REAL_64
		do
			frame_time:= 1/fps_limit
			last_time:= 0
			current_time:= 0
			time_diff:= 0

			if	attached main_window as window then

				game_state := 1

				from
				until
					is_running(window)
				loop

				end

				game_state := 2

				from
				until
				 	not is_running(window)
				loop
					create time.make_now
					current_time:= time.fine_seconds
					time_diff:= current_time-last_time
					-- frame limiter
					if time_diff < frame_time then
						sleep(((frame_time-time_diff)*1000000000).truncated_to_integer_64)
						create time.make_now
						current_time:= time.fine_seconds
						time_diff:= current_time-last_time
					end
					print((1/time_diff).out + "%N")
					last_time:=current_time

					across
						game_root_objects.new_cursor as cursor
					loop
						cursor.item.update
					end

					across
						game_root_objects.new_cursor as cursor
					loop
						cursor.item.draw
					end
				end
				game_state := 0
			else
				print("main_window not attched in main_loop")
			end
		end

feature {NONE}

	is_running(window: separate MAIN_WINDOW):BOOLEAN
		do
			Result:= (game_state /= 0) and (not window.is_destroyed)
		end
end
