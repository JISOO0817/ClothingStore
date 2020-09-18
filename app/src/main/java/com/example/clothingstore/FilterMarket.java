package com.example.clothingstore;

import android.widget.Filter;

import com.example.clothingstore.adapters.AdapterMarket;
import com.example.clothingstore.models.ModelMarket;


import java.util.ArrayList;

public class FilterMarket extends Filter {

    private AdapterMarket adapterMarket;
    private ArrayList<ModelMarket> filterList;

    public FilterMarket(AdapterMarket adapterMarket, ArrayList<ModelMarket> filterList) {
        this.adapterMarket = adapterMarket;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {

        FilterResults results = new FilterResults();

        if(constraint != null && constraint.length()>0){

            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelMarket> filterMarkets = new ArrayList<>();

            for(int i=0; i<filterList.size(); i++){

                if(filterList.get(i).getMarketName().toUpperCase().contains(constraint)){
                    filterMarkets.add(filterList.get(i));
                }
            }

            results.count = filterMarkets.size();
            results.values = filterMarkets;
        }else{
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

        adapterMarket.marketList = (ArrayList<ModelMarket>) results.values;
        adapterMarket.notifyDataSetChanged();

    }
}
