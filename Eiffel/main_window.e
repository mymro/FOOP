note
	description: "Main window for this application"
	author: "Generated by the New Vision2 Application Wizard."
	date: "$Date: 2018/5/16 13:10:8 $"
	revision: "1.0.0"

class
	MAIN_WINDOW

inherit
	EV_TITLED_WINDOW
		rename
			create_interface_objects as create_objects
		redefine
			create_objects,
			initialize,
			is_in_default_state
		end

	INTERFACE_NAMES
		export
			{NONE} all
		undefine
			default_create, copy
		end

create
	default_create

feature {NONE} -- Initialization

	create_objects
			-- <Precursor>
		do
				-- Create main container.
			create main_container
				-- Create the menu bar.
			create standard_menu_bar
				-- Create file menu.
			create file_menu.make_with_text (Menu_file_item)
				-- Create help menu.
			create help_menu.make_with_text (Menu_help_item)

				-- Create a toolbar.
			create standard_toolbar

			create diplay_area.make_with_size (board_width, board_height)

			create pixmap_buffers.make (0)

			buffer_index:=0

			create current_game.make
		end

	initialize
			-- Build the interface for this window.
		do
			Precursor {EV_TITLED_WINDOW}

				-- Create and add the menu bar.
			build_standard_menu_bar
			set_menu_bar (standard_menu_bar)

				-- Create and add the toolbar.
			build_standard_toolbar
			main_container.extend (create {EV_HORIZONTAL_SEPARATOR})
			main_container.disable_item_expand (main_container.first)
			main_container.extend (standard_toolbar)
			main_container.disable_item_expand (standard_toolbar)

			build_main_container
			extend (main_container)


				-- Execute `request_close_window' when the user clicks
				-- on the cross in the title bar.
			close_request_actions.extend (agent request_close_window)

				-- Set the title of the window.
			set_title (Window_title)

				-- Set the initial size of the window.
				-- it is donw that way, because the actual size is
				-- undetermined before all containers are added,
				-- because the height of all bars on the top are unknown
				-- Especially the one with the x
				-- Also there is some margin, which makes the window also wider
			Window_width := current.width
			Window_height := current.height
			launch_game(current_game)
		end

	is_in_default_state: BOOLEAN
			-- Is the window in its default state?
			-- (as stated in `initialize')
		do
			Result := (width = Window_width) and then
				(height = Window_height) and then
				(title.is_equal (Window_title))
		end

feature {NONE} -- Menu Implementation

	standard_menu_bar: EV_MENU_BAR
			-- Standard menu bar for this window.

	file_menu: EV_MENU
			-- "File" menu for this window (contains New, Open, Close, Exit...)

	help_menu: EV_MENU
			-- "Help" menu for this window (contains About...)

	build_standard_menu_bar
			-- Create and populate `standard_menu_bar'.
		do
				-- Add the "File" menu.
			build_file_menu
			standard_menu_bar.extend (file_menu)
				-- Add the "Help" menu.
			build_help_menu
			standard_menu_bar.extend (help_menu)
		ensure
			menu_bar_initialized: not standard_menu_bar.is_empty
		end

	build_file_menu
			-- Create and populate `file_menu'.
		local
			menu_item: EV_MENU_ITEM
		do
			create menu_item.make_with_text (Menu_file_new_item)
				--| TODO: Add the action associated with "New" here.
			file_menu.extend (menu_item)

			create menu_item.make_with_text (Menu_file_open_item)
				--| TODO: Add the action associated with "Open" here.
			file_menu.extend (menu_item)

			create menu_item.make_with_text (Menu_file_save_item)
				--| TODO: Add the action associated with "Save" here.
			file_menu.extend (menu_item)

			create menu_item.make_with_text (Menu_file_saveas_item)
				--| TODO: Add the action associated with "Save As..." here.
			file_menu.extend (menu_item)

			create menu_item.make_with_text (Menu_file_close_item)
				--| TODO: Add the action associated with "Close" here.
			file_menu.extend (menu_item)

			file_menu.extend (create {EV_MENU_SEPARATOR})

				-- Create the File/Exit menu item and make it call
				-- `request_close_window' when it is selected.
			create menu_item.make_with_text (Menu_file_exit_item)
			menu_item.select_actions.extend (agent request_close_window)
			file_menu.extend (menu_item)
		ensure
			file_menu_initialized: not file_menu.is_empty
		end

	build_help_menu
			-- Create and populate `help_menu'.
		local
			menu_item: EV_MENU_ITEM
		do
			create menu_item.make_with_text (Menu_help_contents_item)
				--| TODO: Add the action associated with "Contents and Index" here.
			help_menu.extend (menu_item)

			create menu_item.make_with_text (Menu_help_about_item)
			menu_item.select_actions.extend (agent on_about)
			help_menu.extend (menu_item)

		ensure
			help_menu_initialized: not help_menu.is_empty
		end

feature {NONE} -- ToolBar Implementation

	standard_toolbar: EV_TOOL_BAR
			-- Standard toolbar for this window.

	build_standard_toolbar
			-- Populate the standard toolbar.
		do
				-- Initialize the toolbar.
			standard_toolbar.extend (new_toolbar_item ("New", "new.png"))
			standard_toolbar.extend (new_toolbar_item ("Open", "open.png"))
			standard_toolbar.extend (new_toolbar_item ("Save", "save.png"))
		ensure
			toolbar_initialized: not standard_toolbar.is_empty
		end

	new_toolbar_item (name: READABLE_STRING_GENERAL; image: READABLE_STRING_GENERAL): EV_TOOL_BAR_BUTTON
			-- A new toolbar item with an image from a file `image' or with a text `name' if image is not available.
		local
			toolbar_pixmap: EV_PIXMAP
		do
			if attached Result then
					-- Image could not be loaded.
					-- Use a text label instead.
				Result.set_text (name)
			else
					-- The first attempt to create a button from an image file.
				create Result
				create toolbar_pixmap
				toolbar_pixmap.set_with_named_file (image)
					-- Make sure the image is effectively loaded by computing its dimention.
				toolbar_pixmap.height.do_nothing
					-- Everything is OK, associate image with the button.
				Result.set_pixmap (toolbar_pixmap)
			end
		rescue
			if attached Result then
					-- Image could not be loaded.
					-- Create a button by setting a label text instead.
				retry
			end
		end

feature {NONE} -- About Dialog Implementation

	on_about
			-- Display the About dialog.
		local
			about_dialog: ABOUT_DIALOG
		do
			create about_dialog
			about_dialog.show_modal_to_window (Current)
		end

feature {NONE} -- Implementation, Close event

	request_close_window
			-- Process user request to close the window.
		local
			question_dialog: EV_CONFIRMATION_DIALOG
		do
			create question_dialog.make_with_text (Label_confirm_close_window)
			question_dialog.show_modal_to_window (Current)

			if question_dialog.selected_button ~ (create {EV_DIALOG_CONSTANTS}).ev_ok then
					-- Destroy the window.
				destroy

					-- End the application.
					--| TODO: Remove next instruction if you don't want the application
					--|       to end when the first window is closed..
					-- set game_state.state to 0 indicating the game has ended
					shut_down_game(current_game)
				if attached (create {EV_ENVIRONMENT}).application as a then
					a.destroy
				end
			end
		end

feature {NONE} -- Implementation

	main_container: EV_VERTICAL_BOX
			-- Main container (contains all widgets displayed in this window).

	build_main_container
			-- Populate `main_container'.
		local
			container: EV_VERTICAL_BOX
		do
			--diplay_area.set_foreground_color (create {EV_COLOR}.make_with_rgb(1,0,0))
			--diplay_area.fill_rectangle (0, 0, board_width, board_height)
			--pixmap.pointer_enter_actions.extend (agent enter)
			--pixmap.pointer_leave_actions.extend (agent leave)
			--pixmap.pointer_button_press_actions.extend (agent press)
			create container
			container.set_minimum_size (board_width, board_height)
			container.extend (diplay_area)
			main_container.extend (container)
		ensure
			main_container_created: main_container /= Void
		end

feature {NONE} --functions for mouse interaction testing

	enter
		do
			diplay_area.set_foreground_color(create {EV_COLOR}.make_with_rgb(1,0,0))
			diplay_area.fill_rectangle (0, 0, board_width, board_height)
		end

	leave
		do
			diplay_area.set_foreground_color(create {EV_COLOR}.make_with_rgb(0,1,0))
			diplay_area.fill_rectangle (0, 0, board_width, board_height)
		end

	press(x: INTEGER_32; y: INTEGER_32; button: INTEGER_32; x_tilt: REAL_64; y_tilt: REAL_64; pressure: REAL_64; x_screen: INTEGER_32; y_screen: INTEGER_32)
		do
			diplay_area.set_foreground_color(create {EV_COLOR}.make_with_rgb(0,0,1))
			diplay_area.fill_rectangle (x, y, 10, 10)
		end

feature {NONE} -- al functions concerning the state of the game


	launch_game(arg: separate GAME)
		--sets up the variables and launches the game
		do
			arg.set_up (Current, board_width, board_height)
			arg.launch
		end

	shut_down_game(game: separate GAME)
	-- shuts down the game
		do
			game.shut_down
		end

feature {ANY}-- interfaces for GAME

	create_pixmap_buffer(a_width, a_height: INTEGER_32):INTEGER
	-- creates a buffer and returns the index
		local
			buffer: EV_PIXMAP_ADVANCED
		do
			from
			until
				pixmap_buffers.at (buffer_index) = void
			loop
				buffer_index := buffer_index + 1
			end
			create buffer.make_with_size (a_width, a_height)
			pixmap_buffers.put (buffer, buffer_index)
			RESULT:=buffer_index
		end

	create_pixmap_buffer_from_image(image: separate READABLE_STRING_8):INTEGER
	-- creates a buffer from an image
	-- loads missing_image.png, if image not found
		local
			buffer: EV_PIXMAP_ADVANCED
			path_to_image: STRING
			rescue_path: STRING
		do
			from
			until
				pixmap_buffers.at (buffer_index) = void
			loop
				buffer_index := buffer_index + 1
			end
			create buffer
			if attached rescue_path as rescue_image then
				create path_to_image.make_from_string (rescue_image)
			else
				create path_to_image.make_from_separate (image)
			end
			buffer.set_with_named_file (path_to_image)
			buffer.height.do_nothing
			pixmap_buffers.put (buffer, buffer_index)
			RESULT:=buffer_index
		rescue
			rescue_path:="images\missing_image.png"
			retry
		end

	draw_buffer_to_display(i: INTEGER; pos_x, pos_y: INTEGER)
	-- draws a buffer at top left position pos
		do
			if attached get_pixmap_buffer(i) as buffer then
				diplay_area.draw_pixmap (pos_x, pos_y, buffer)
			else
				print(i.out + " buffer not existing in draw buffer")
			end
		end


	get_pixmap_buffer(i:INTEGER):detachable EV_PIXMAP_ADVANCED
	-- returns a buffer if it exists otherwhise void
		do
			RESULT:=pixmap_buffers.at (i)
		end

feature {NONE} -- variables

	Window_title: STRING = "my_vision2_application_1"

	Window_width: INTEGER
	Window_height: INTEGER

	board_width: INTEGER = 500
	board_height: INTEGER = 500

	diplay_area: EV_PIXMAP_ADVANCED
	current_game: separate GAME
	pixmap_buffers: HASH_TABLE[EV_PIXMAP_ADVANCED, INTEGER]
	buffer_index: INTEGER
end
