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
	main_window: detachable separate MAIN_WINDOW
	fps_limit: INTEGER = 60
	labyrinth_node_size: INTEGER = 30
	draw_queue_pos: LINKED_LIST[VECTOR_2]
	draw_queue_buffer_index: LINKED_LIST[INTEGER]
	node_type_helper: NODE_TYPE_BASE

feature{ANY}
	delta_time: REAL_64
	labyrinth_nodes_x: INTEGER
	labyrinth_nodes_y: INTEGER
	draw_area_height: INTEGER
	draw_area_width: INTEGER
	was_new_key_pressed: BOOLEAN
	last_pressed_key: INTEGER
	game_root_object: detachable GAME_OBJECT
	player_count: INTEGER
	has_player_found_exit: BOOLEAN
	is_game_running: BOOLEAN

feature {NONE}

	make
	-- seeding random number generator
	-- and resetting state
		local
			time:TIME
		do
			create time.make_now
			create rand.set_seed (time.seconds)
			draw_area_height:= 0
			draw_area_width:=0
			labyrinth_nodes_x:= 0
			labyrinth_nodes_y:= 0
			delta_time:=0
			player_count:=0
			was_new_key_pressed:= FALSE
			last_pressed_key:=0
			create draw_queue_pos.make
			create draw_queue_buffer_index.make
			create node_type_helper
			has_player_found_exit:= FALSE
			is_game_running:= FALSE
		end

feature {NONE}

	is_destroyed(window: separate MAIN_WINDOW):BOOLEAN
		do
			Result:= window.is_destroyed
		end

	update_pressed_key(window: separate MAIN_WINDOW)
		do
			was_new_key_pressed:= window.was_new_key_pressed
			last_pressed_key:=window.get_last_pressed_key
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

feature{ANY}
	draw_once
		do
			if attached game_root_object as root and
				attached main_window as window then
				root.draw
				draw_queue_to_display(window)
			else
				print("game_root_object or main_window not attached%N")
			end
		end

feature {ANY}

	is_window_attached:BOOLEAN
		do
			RESULT:= attached main_window
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

	player_has_found_exit(player: Player)
		do
			has_player_found_exit := TRUE
		end

feature {ANY}

	add_player(name: separate STRING; color: separate EV_COLOR)
	-- adds a player at a random location
	 	require
	 		labyrinth_nodes_x > 0
	 		labyrinth_nodes_y > 0
	 		attached {MAIN_LABYRINTH} game_root_object
	 		not is_game_running
		local
			cursor: PLAYER
			robot: ROBOT
			x: INTEGER
			y: INTEGER
			key_bindings: KEY_BINDINGS
		do
			if player_count < 2 then
				if attached {MAIN_LABYRINTH} game_root_object as root then
					from
						rand.forth
						x:= (rand.item\\labyrinth_nodes_x)+1
						rand.forth
						y:= (rand.item\\labyrinth_nodes_y)+1
					until
						not root[x,y].type.is_of_type (node_type_helper.type_finish)
					loop
						rand.forth
						x:= (rand.item\\labyrinth_nodes_x)+1
						rand.forth
						y:= (rand.item\\labyrinth_nodes_y)+1
					end

					inspect player_count
					when 0 then
						key_bindings:=  create {PLAYER_1_KEY_BINDINGS}
					when 1 then
						key_bindings:=  create {PLAYER_2_KEY_BINDINGS}
					end

					create cursor.make (current, root, create{VECTOR_2}.make_with_xy (x, y), key_bindings)
					create robot.make_robot (current, root , create{VECTOR_2}.make_with_xy (x, y), create {EV_COLOR}.make_with_rgb (color.red, color.green, color.blue), create{STRING}.make_from_separate (name))

					root.add_child (cursor)
					root.add_child (robot)

					player_count := player_count + 1
					draw_once
				else
					print("Root as MAIN_LABYRINTH not attached%N")
				end
			else
				print("already 2 palyers%N")
			end
		end

	set_up(window: separate MAIN_WINDOW; drawing_area_width, drawing_area_height:INTEGER)
	--attaches nedded objects for game
	--creates root object
	require
		attached window
		not is_game_running
	local
		root: MAIN_LABYRINTH
	do
		main_window:=window
		draw_area_height:= drawing_area_height
		draw_area_width:= drawing_area_width
		labyrinth_nodes_x:= (draw_area_width/labyrinth_node_size).truncated_to_integer
		labyrinth_nodes_y:= (draw_area_height/labyrinth_node_size).truncated_to_integer


		create root.create_new_labyrinth(Current, create{VECTOR_2}.make_with_xy (labyrinth_nodes_x, labyrinth_nodes_y), create{VECTOR_2}.make_with_xy (0, 0),  create{VECTOR_2}.make_with_xy (drawing_area_width, drawing_area_height))
		game_root_object:=root
	end

	launch
	--launches the game
	-- the main game loop
		require
			player_count > 0
			not is_game_running
		local
			time: TIME
			last_time: REAL_64
			current_time: REAL_64
			frame_time: REAL_64
		do
			has_player_found_exit:= FALSE
			frame_time:= 1/fps_limit
			last_time:= 0
			current_time:= 0
			delta_time:= 0

			if	attached main_window as window
				and attached game_root_object as root then
					is_game_running:= TRUE
				from
				until
				 	is_destroyed(window) or
				 	has_player_found_exit
				loop
					create time.make_now
					current_time:= time.fine_seconds
					delta_time:= current_time-last_time
					-- frame limiter
					if delta_time < frame_time then
						sleep(((frame_time-delta_time)*1000000000).truncated_to_integer_64)
						create time.make_now
						current_time:= time.fine_seconds
						delta_time:= current_time-last_time
					end
					--print((1/delta_time).out + "%N")
					last_time:=current_time

					update_pressed_key(window)

					root.update
					root.draw

					draw_queue_to_display(window)
				end
			else
				print("main_window not attched in main_loop")
			end
		end
end
