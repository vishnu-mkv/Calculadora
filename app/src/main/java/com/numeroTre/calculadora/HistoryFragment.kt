package com.numeroTre.calculadora

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.numeroTre.calculadora.data.Expression
import com.numeroTre.calculadora.data.ExpressionDatabase
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [HistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistoryFragment : BaseFragment(R.layout.fragment_history), RecentExpressionAdapter.OnItemClickListener{

    val historyList = mutableListOf<Expression>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        recyclerView.apply{
            this.adapter = RecentExpressionAdapter(historyList, this@HistoryFragment)
            this.layoutManager = LinearLayoutManager(activity)


        launch {
            context?.let {
                    historyList.addAll(ExpressionDatabase(it).getExpressionDao().getAllExpressions())
                    recyclerView.adapter!!.notifyDataSetChanged()
                    recyclerView.layoutManager!!.scrollToPosition(recyclerView.adapter!!.itemCount -1)

                }
            }
        }

        BtnBack.setOnClickListener{
            activity!!.onBackPressed()
        }

        BtnDelete.setOnClickListener{
            val dialogClickListener = DialogInterface.OnClickListener(){
                dialog: DialogInterface, which: Int ->
                run {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        (activity as MainActivity).clearHistory()
                        dialog.dismiss()
                        activity!!.onBackPressed()
                    }

                    if (which == DialogInterface.BUTTON_NEGATIVE){
                        dialog.dismiss()
                    }
                }
            }

            AlertDialog.Builder(context).setTitle("Clear History")
                    .setMessage("Are you ure to delete all history?")
                    .setPositiveButton("Confirm", dialogClickListener)
                    .setNegativeButton("Cancel", dialogClickListener).show()
        }
    }



    override fun onItemClick(position: Int) {
        (activity as MainActivity).loadFromHistory = true
        (activity as MainActivity).historyExpressionObject = historyList[position]
        activity!!.onBackPressed()
    }
}