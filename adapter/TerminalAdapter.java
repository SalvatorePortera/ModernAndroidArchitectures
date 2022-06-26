/*
 * Copyright (c) 2019 - 2020 AB Circle Limited. All rights reserved
 */

package com.nereus.craftbeer.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import java.util.List;

import javax.smartcardio.CardTerminal;

public class TerminalAdapter extends ArrayAdapter<String>
{

    private List<CardTerminal> mTerminals;

    public TerminalAdapter(@NonNull Context context, @LayoutRes int resource)
    {
        super(context, resource);
    }

    public void updateTerminals(List<CardTerminal> mTerminals)
    {
        clear();
        this.mTerminals = mTerminals;
        for (CardTerminal terminal : mTerminals)
        {
            add(terminal.getName());
        }
    }

    public CardTerminal getTerminal(int index)
    {
        return mTerminals.get(index);
    }

    @Override
    public void clear()
    {
        super.clear();
    }
}
