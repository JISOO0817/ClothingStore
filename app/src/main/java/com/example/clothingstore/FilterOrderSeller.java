package com.example.clothingstore;

import android.widget.Filter;

import com.example.clothingstore.adapters.AdapterOrderSeller;
import com.example.clothingstore.adapters.AdapterProductUser;
import com.example.clothingstore.models.ModelOrderSeller;
import com.example.clothingstore.models.ModelProduct;

import java.util.ArrayList;

public class FilterOrderSeller extends Filter {

    private AdapterOrderSeller adapter;
    private ArrayList<ModelOrderSeller>filterList;

    public FilterOrderSeller(AdapterOrderSeller adapter, ArrayList<ModelOrderSeller> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {

        FilterResults results = new FilterResults();
        //검색 쿼리를 위한 유효성 검사

        if(constraint != null && constraint.length()>0){


            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelOrderSeller> filteredModels = new ArrayList<>();
            for(int i=0; i<filterList.size(); i++){
                //타이틀 & 카테고리로 검색
                if(filterList.get(i).getOrderStatus().toUpperCase().contains(constraint))
                {
                    filteredModels.add(filterList.get(i));
                }
            }

            results.count = filteredModels.size();
            results.values = filteredModels;
        }else{

            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.modelOrderSellerArrayList = (ArrayList<ModelOrderSeller>)  results.values;
        adapter.notifyDataSetChanged();

    }

}
