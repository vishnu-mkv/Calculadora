package com.numeroTre.calculadora

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.numeroTre.calculadora.data.Expression
import kotlinx.android.synthetic.main.item_recent_expressions.view.*

open class RecentExpressionAdapter(var expressionList: List<Expression>,
                                   private val listener:OnItemClickListener):
        RecyclerView.Adapter<RecentExpressionAdapter.ExpressionViewHolder>(){
    inner class ExpressionViewHolder(expressionView: View): RecyclerView.ViewHolder(expressionView),
            View.OnClickListener{
        init {
            expressionView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position: Int = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpressionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recent_expressions,parent,false)
        return ExpressionViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpressionViewHolder, position: Int) {
        holder.itemView.apply{
            tvRecentExpression.text = expressionList[position].expression
            tvResult.text = "= " + expressionList[position].result
        }
    }

    override fun getItemCount(): Int {
        return expressionList.size
    }

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }
}