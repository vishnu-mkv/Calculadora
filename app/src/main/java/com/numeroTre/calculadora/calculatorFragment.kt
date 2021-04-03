package com.numeroTre.calculadora

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_calculator.*

/**
 * A simple [Fragment] subclass.
 * Use the [calculatorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class calculatorFragment : Fragment(R.layout.fragment_calculator) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        BtnOne.setOnClickListener{
            TvEquationBuffer.text = TvEquationBuffer.text.toString() + "1"
        }
    }
}