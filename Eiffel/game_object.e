note
	description: "basic game_object functionality"
	author: "Constantin Budin"
	date: "19.05.2018"
	revision: "0.9"

class
	GAME_OBJECT

create
	make

feature {ANY}
	layer: INTEGER
	parent: detachable GAME_OBJECT assign set_parent
	game: GAME

feature {NONE}
	pos_relative_to_parent: VECTOR_2
	buffer_indices:ARRAY[INTEGER]
	dimension: VECTOR_2
	children: ARRAYED_LIST[GAME_OBJECT]

feature {NONE}
	make(a_game: GAME; a_pos_relative_to_parent, a_dimension: VECTOR_2; a_layer, buffer_count:INTEGER)
		require
			a_dimension.x>=0
			a_dimension.y>=0
			buffer_count > 0
		do
			initialize(a_game, a_pos_relative_to_parent, a_dimension, a_layer)
			create buffer_indices.make_filled (0, 1, buffer_count)

			across
				1 |..| buffer_count as i
			loop
				buffer_indices[i.item]:= game.create_buffer(dimension)
			end
		end

	make_with_buffers(a_game: GAME; a_pos_relative_to_parent, a_dimension: VECTOR_2; a_layer:INTEGER; some_buffer_indices: ARRAY[INTEGER])
		require
			a_dimension.x>=0
			a_dimension.y>=0
		do
			initialize(a_game, a_pos_relative_to_parent, a_dimension, a_layer)
			buffer_indices := some_buffer_indices
		end

	initialize(a_game: GAME; a_pos_relative_to_parent, a_dimension:VECTOR_2; a_layer: INTEGER)
		require
			a_dimension.x>=0
			a_dimension.y>=0
		do
			game:= a_game
			parent:= void
			pos_relative_to_parent:= a_pos_relative_to_parent
			layer:= a_layer
			dimension:=a_dimension
			create children.make (0)
		end

	set_dimension(a_pixmap: separate EV_PIXMAP_ADVANCED)
	-- sets the dimension based on the size of the buffer
		do
			dimension.x:=a_pixmap.width
			dimension.y:=a_pixmap.height
		end

	draw_buffer_at_index(index: INTEGER)
	-- draws the buffer at index to screen
	-- at absolute parent position plus relative position to parent
		do
			game.queue_buffer_for_draw_to_display (buffer_indices[index], get_absolute_pos)
		end

feature {ANY}

	get_absolute_pos: VECTOR_2
	-- returns the absolute pos of object in window
		do
			if attached parent as a_parent then
				RESULT:= pos_relative_to_parent + a_parent.get_absolute_pos
			else
				RESULT:= pos_relative_to_parent
			end
		end

	add_child(child: GAME_OBJECT)
	-- adds a child, sets the parent to current
	-- and removes child from old parent
		do
			if children.count = 0 then
				children.extend (child)
				child.parent:= current
			elseif children.occurrences (child) = 0 then
				from
					children.start
				until
					children.item_for_iteration.layer > child.layer or children.islast
				loop
					children.forth
				end

				if children.item.layer > child.layer then
					children.put_left (child)
				else
					children.put_right (child)
				end
				child.parent:= current
			end
		ensure
			children.occurrences (child) = 1
			child.parent = current
		end

	remove_all_children
		do
			from

			until
				children.is_empty
			loop
				remove_child(children.first)
			end
		end

	remove_child(child: GAME_OBJECT)
	-- removes child and sets child.parent to void
		do
			children.prune_all (child)
			child.parent:=void
		ensure
			children.occurrences (child) = 0
			child.parent = void
		end

	set_parent(a_parent: detachable GAME_OBJECT)
	-- sets parent, adds child to new parent and removes child from old parent.
	-- always use this function to set parent
		do
			if(a_parent /= current.parent) then
				if attached parent as old_parent then
					parent:=void
					old_parent.remove_child(current)
				end
				if attached a_parent as new_parent then
					parent:=new_parent
					new_parent.add_child (current)
				end
			end
		ensure
			current.parent = a_parent
		end

	is_visible_in_drawing_area:BOOLEAN
	-- is a part of object visible in the drawing_are
	-- checks based on current dimension and absolute position
		local
			pos:VECTOR_2
		do
			pos:=get_absolute_pos
			if 	pos.x > game.draw_area_width or
				pos.y > game.draw_area_height or
				pos.x < -dimension.x or
				pos.y < -dimension.y then
				RESULT:= FALSE
			else
				RESULT:= TRUE
			end
		end

	draw
	-- draws the object to screen and its children
		do
			if children.count > 0 then
				across
					children.new_cursor as cursor
				loop
					cursor.item.draw
				end
			end
		end

	update
	-- updates object and its children
		do
			if children.count > 0 then
				across
					children.new_cursor as cursor
				loop
					cursor.item.update
				end
			end
		end
end
