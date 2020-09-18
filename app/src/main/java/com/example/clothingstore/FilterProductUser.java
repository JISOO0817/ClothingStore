package com.example.clothingstore;

import android.widget.Filter;

import com.example.clothingstore.adapters.AdapterProductSeller;
import com.example.clothingstore.adapters.AdapterProductUser;
import com.example.clothingstore.models.ModelProduct;

import java.util.ArrayList;

public class FilterProductUser extends Filter {

    private AdapterProductUser adapter;
    private ArrayList<ModelProduct>filterList;

    public FilterProductUser(AdapterProductUser adapter, ArrayList<ModelProduct> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {

        FilterResults results = new FilterResults();
        //검색 쿼리를 위한 유효성 검사

        if(constraint != null && constraint.length()>0){


            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelProduct> filteredModels = new ArrayList<>();
            for(int i=0; i<filterList.size(); i++){
                //타이틀 & 카테고리로 검색
                if(filterList.get(i).getProductTitle().toUpperCase().contains(constraint) ||
                filterList.get(i).getProductCategory().toUpperCase().contains(constraint)){
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
        adapter.productsList = (ArrayList<ModelProduct>)  results.values;
        adapter.notifyDataSetChanged();

    }

}
