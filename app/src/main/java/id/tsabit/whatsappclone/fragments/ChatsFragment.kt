package id.tsabit.whatsappclone.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import id.tsabit.whatsappclone.ModelClasses.ChatList
import id.tsabit.whatsappclone.ModelClasses.Users
import id.tsabit.whatsappclone.R
import id.tsabit.whatsappclone.adapters.UserAdapter

class ChatsFragment : Fragment() {

    private var userAdapter: UserAdapter? = null
    private var mUsers: List<Users>? = null
    private var userChatList: List<ChatList>? = null
    lateinit var recyclerview_chat_list: RecyclerView
    private var firebaseUser: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chats, container, false)

        recyclerview_chat_list = view.findViewById(R.id.recyclerview_chat_list)
        recyclerview_chat_list.setHasFixedSize(true)
        recyclerview_chat_list.layoutManager = LinearLayoutManager(context)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        userChatList = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid)
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (userChatList as ArrayList).clear()
                for (dataSnapshot in snapshot.children){
                    val chatList = dataSnapshot.getValue(ChatList::class.java)

                    (userChatList as ArrayList).add(chatList!!)
                }
                retrieveChatList()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        return view
    }

    private fun retrieveChatList(){
        mUsers = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("Users")
        ref.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                (mUsers as ArrayList<Users>).clear()
                for (dataSnapshot in snapshot.children){
                    val user = dataSnapshot.getValue(Users::class.java)

                    for (eachChatList in userChatList!!){
                        if (user!!.getUid().equals(eachChatList.getId())){
                            (mUsers as ArrayList).add(user!!)
                        }
                     }
                }
                userAdapter = UserAdapter(context!!, (mUsers as ArrayList<Users>), true)
                recyclerview_chat_list.adapter = userAdapter
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
}