package com.muvasia.driver.androidarchitecturecomponents.view.fragments


import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.muvasia.driver.androidarchitecturecomponents.R
import com.muvasia.driver.androidarchitecturecomponents.adapter.NoteAdapter
import com.muvasia.driver.androidarchitecturecomponents.database.Note
import com.muvasia.driver.androidarchitecturecomponents.viewModel.NoteViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class NoteListFragment : Fragment() {

    private lateinit var noteViewModel: NoteViewModel

    companion object {
        val TAG = NoteListFragment::class.simpleName
        const val NOTE_ID = "NOTE_ID"

        fun newInstance(): NoteListFragment {
            return NoteListFragment()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_note_list, container, false)

        val activity = activity
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_note_list)
        val adapter = NoteAdapter(activity!!)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel::class.java)


        noteViewModel.allNotes.observe(viewLifecycleOwner, Observer { notes ->
            adapter.setNoteList(notes!!)
        })


        /**
         * for update a note
         *
         */
        adapter.setOnItemClickListener(object : NoteAdapter.OnItemClickListener {
            override fun OnItemClick(note: Note) {
                Log.d(TAG, "id:: " + note.id)

                val updateNoteFragment = UpdateNoteFragment()
                val bundle = Bundle()
                bundle.putInt("NOTE_ID", note.id)
                updateNoteFragment.arguments = bundle

                activity.supportFragmentManager
                    .beginTransaction()
                    .addToBackStack(UpdateNoteFragment.TAG)
                    .replace(R.id.fragment_container, updateNoteFragment, UpdateNoteFragment.TAG)
                    .commit()
            }
        })


        /**
         * On swipe left or right delete a note
         *
         */
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                noteViewModel.delete(adapter.getNoteAt(viewHolder.adapterPosition))
                Toast.makeText(getActivity(), "Note deleted", Toast.LENGTH_LONG).show()
            }
        }).attachToRecyclerView(recyclerView)

        return view

    }


}
