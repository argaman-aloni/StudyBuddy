package com.technion.studybuddy.Views.cards;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;

import com.fima.cardsui.objects.Card;
import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.objects.RecyclableCard;
import com.fima.cardsui.views.CardUI;

public abstract class BaseCard extends RecyclableCard
{

	private CardUI cardUI;
	protected CardStack stack;

	/**
	 * @param titlePlay
	 * @param description
	 * @param color
	 * @param titleColor
	 * @param hasOverflow
	 * @param isClickable
	 */

	/**
	 * @param cardUI
	 * @param stack
	 */
	public BaseCard(CardUI cardUI, CardStack stack)
	{
		super("");
		this.cardUI = cardUI;
		this.stack = stack;
	}

	protected boolean isOnTop()
	{
		ArrayList<Card> cards = stack.getCards();
		return this == cards.get(cards.size() - 1);
	}

	protected Context getContext()
	{
		return cardUI.getContext();
	}

	protected View getCardCurrentView()
	{
		return mCardLayout;
	}
}
