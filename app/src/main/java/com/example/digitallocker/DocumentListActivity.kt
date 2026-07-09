package com.example.digitallocker

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList


class DocumentListActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var list: ArrayList<Document>
    lateinit var adapter: DocumentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_list)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        list = ArrayList()
        adapter = DocumentAdapter(list)

        recyclerView.adapter = adapter

        fetchData()
    }

    private fun fetchData() {

        val db = FirebaseDatabase.getInstance().reference.child("documents")
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid

        db.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                list.clear()

                for (data in snapshot.children) {

                    val doc = data.getValue(Document::class.java)

                    if (doc != null && doc.userId == currentUser) {
                        list.add(doc)
                    }
                }

                list.sortByDescending { it.timestamp ?: 0 }

                // 🔥 FORCE REFRESH
                adapter = DocumentAdapter(list)
                recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}