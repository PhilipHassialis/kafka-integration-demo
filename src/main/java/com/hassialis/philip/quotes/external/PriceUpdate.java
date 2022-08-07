package com.hassialis.philip.quotes.external;

import java.math.BigDecimal;

public class PriceUpdate {
	private String symbol;
	private BigDecimal lastPrice;


	public PriceUpdate(String symbol, BigDecimal lastPrice) {
		this.symbol = symbol;
		this.lastPrice = lastPrice;
	}

	public PriceUpdate() {
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public BigDecimal getLastPrice() {
		return lastPrice;
	}

	public void setLastPrice(BigDecimal lastPrice) {
		this.lastPrice = lastPrice;
	}

	@Override
	public String toString() {
		return "PriceUpdate{" +
			"symbol='" + symbol + '\'' +
			", lastPrice=" + lastPrice +
			'}';
	}
}
