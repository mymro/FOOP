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
	draw_queue_pos: LINKED_LIST[VECTOR_2]
	draw_queue_buffer_index: LINKED_LIST[INTEGER]

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
		-- TODO create static variables for states

feature {NONE}

	make
	-- seeding random number generator
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
			create game_root_objects.make(0)
			create draw_queue_pos.make
			create draw_queue_buffer_index.make
		end

	draw_queue_to_display(window: separate MAIN_WINDOW)
	--draws the queue to display
		require
			 draw_queue_pos.count = draw_queue_buffer_index.count
		local
		do
			if draw_queue_pos.count > 0 then
				from
					draw_queue_pos.start
					draw_queue_buffer_index.start
					window.draw_buffer_to_display (draw_queue_buffer_index.item, draw_queue_pos.item.x, draw_queue_pos.item.y)
				until
					draw_queue_pos.islast
				loop
					draw_queue_pos.forth
					draw_queue_buffer_index.forth
					window.draw_buffer_to_display (draw_queue_buffer_index.item, draw_queue_pos.item.x, draw_queue_pos.item.y)
				end
				draw_queue_pos.wipe_out
				draw_queue_buffer_index.wipe_out
			end
		ensure
			draw_queue_pos.count = draw_queue_buffer_index.count
		end

	get_buffer_i(i:INTEGER; window: separate MAIN_WINDOW): detachable separate EV_PIXMAP_ADVANCED
	--implementation of get_buffer
		do
			RESULT:=window.get_pixmap_buffer(i)
		end

	create_buffer_i(a_dimension:VECTOR_2; window: separate MAIN_WINDOW):INTEGER
	-- creates a buffer and returns its index implementation
		require
			a_dimension.x > 0
			a_dimension.y > 0
		do
			RESULT:= window.create_pixmap_buffer (a_dimension.x, a_dimension.y)
		end

	create_buffer_from_image_i(path_to_image: READABLE_STRING_8; window: separate MAIN_WINDOW):INTEGER
	-- creates a buffer from image with path and returns its index implementation
		do
			RESULT:= window.create_pixmap_buffer_from_image (path_to_image)
		end

	set_mask_i(index_target, index_mask:INTEGER; window: separate MAIN_WINDOW)
	-- sets a mask fro a pixmap in buffer implementation
		do
			window.set_mask (index_target, index_mask)
		end

feature {ANY}

	is_window_attached:BOOLEAN
		do
			RESULT:= attached main_window
		end

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

	create_buffer(a_dimension:VECTOR_2):INTEGER
	--creates buffer with size
	-- 0 if unsuccessful(index of default image)
		require
			a_dimension.x > 0
			a_dimension.y > 0
			is_window_attached
		do
			if attached main_window as window then
				RESULT:=create_buffer_i(a_dimension, window)
			else
				RESULT:=0
			end
		end

	create_buffer_from_image(image: READABLE_STRING_8):INTEGER
	--creates buffer from image
	-- 0 if unsuccessful(index of default image)
		require
			is_window_attached
		do
			if attached main_window as window then
				RESULT:=create_buffer_from_image_i(image, window)
			else
				RESULT:=0
			end
		end

	queue_buffer_for_draw_to_display(index: INTEGER; pos:VECTOR_2)
	-- queues buffer for draw
		do
			draw_queue_pos.extend (pos)
			draw_queue_buffer_index.extend (index)
		ensure
			draw_queue_pos.count = draw_queue_buffer_index.count
		end

	set_mask(index_target, index_mask:INTEGER)
	-- sets mask of pixmap
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
		flag: FLAG_DONT_COME_NEAR
		flag2: FLAG_SEARCH_HERE
		root: MAIN_LABYRINTH
		robot: ROBOT
	do
		main_window:=window
		draw_area_height:= drawing_area_height
		draw_area_width:= drawing_area_width
		labyrinth_nodes_x:= (draw_area_width/labyrinth_node_size).truncated_to_integer
		labyrinth_nodes_y:= (draw_area_height/labyrinth_node_size).truncated_to_integer

		-- game_objects
		create root.create_new_labyrinth(Current, create{VECTOR_2}.make_with_xy (labyrinth_nodes_x, labyrinth_nodes_y), create{VECTOR_2}.make_with_xy (0, 0),  create{VECTOR_2}.make_with_xy (drawing_area_width, drawing_area_height))
		add_root(root)

		create flag.make_flag (current, create{VECTOR_2}.make_with_xy (10, 10), root)
		root.add_child (flag)

		create flag2.make_flag (current, create{VECTOR_2}.make_with_xy (12, 12), root)
		root.add_child (flag2)

		create robot.make_robot (current, create{VECTOR_2}.make_with_xy (1, 1), root)
		root.add_child (robot)
	end

	launch
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
					draw_queue_to_display(window)
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
