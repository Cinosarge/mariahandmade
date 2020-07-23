package com.mh.model.javaBeans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * In nome dell'efficienza di accesso ad database, il popolamento del carrello viene diviso in due
 * momenti distinti.
 * 
 * Di un prodotto aggiunto al carrello viene salvato dapprima solo il codice e il numero di unità
 * richieste (in dipendenza da quante volte il prodotto viene aggiunto al carrello) nello scope
 * della sessione.
 * 
 * Successivamente vengono caricati i dati dei prodotti solo quando il carrello viene visualizzato.
 * Questo consente un accesso unico al database al solo costo di un controllo (postumo) sulla quantità
 * di prodotto richiesta a fronte delle scorte disponibili.
 * 
 * @author Enrico Sbrighi
 *
 */
public class Cart implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/*
	 * La chiave è rappresentata dal codice del prodotto mentre il valore è un prodotto
	 * aggiunto al carrello.
	 */
	Map<Integer, CartProduct> cartProductMap;

	public Cart(){
		cartProductMap = new HashMap<Integer, CartProduct>();
	}
	
	public Map<Integer, CartProduct> getCartProductMap() {
		return cartProductMap;
	}

	public void setCartProductMap(Map<Integer, CartProduct> products) {
		this.cartProductMap = products;
	}
	
	/**
	 * Aggiunge un prodotto al carrello. Se il prodotto è già presente ne incrementa
	 * la quantità.
	 * 
	 * @param productCode
	 */
	public void putProduct(Integer productCode) {
		if(!cartProductMap.containsKey(productCode)) {
			CartProduct cartProduct = new CartProduct();
			cartProduct.setDeal(1);
			cartProductMap.put(productCode, cartProduct);
		}
		else {
			CartProduct cartProduct = cartProductMap.get(productCode);
			
			/*
			 * Non vengono aggiunte le informazioni sul prodotto per evitare un accesso al database
			 * ad ogni aggiunta di un prodotto al carrello. Si noti quindi che il numero di scorte
			 * disponibili per un determinato prodotto non è noto al momento e quindi il numero di
			 * unità scelte per l'acquisto può crescere oltre questo numero. Tuttavia quando vengono
			 * mostrati i dati nel carrello si effettua questo controllo e le relative correzioni.
			 * 
			 * @see putProductWithData()
			 */
			cartProduct.setDeal(cartProduct.getDeal() + 1);
		}
	}
	
	/**
	 * Aggiunge un prodotto al carrello. Se il prodotto è già presente ne incrementa
	 * la quantità. Aggiunge anche le informazioni a corredo del relativo prodotto e
	 * corregge il numero di oggetti ordinati sulla base delle scorte disponibili.
	 * 
	 * @param product
	 */
	public void putProductWithData(Product product) {
		
		this.putProduct(product.getCode());
		
		CartProduct cartProduct = cartProductMap.get(product.getCode());
		cartProduct.setProduct(product);
		
		/*
		 * Correggiamo il numero di oggetti ordinati se questo soverchia il numero di
		 * scorte disponibili per il particolare prodotto.
		 */
		int deal = cartProduct.getDeal();
		int availableUnits = product.getAvailableUnits();
		
		if(deal <= availableUnits) {
			cartProduct.setDeal(deal);
		}
		else {
			cartProduct.setDeal(availableUnits);
		}
	}
	
	/**
	 * Da usare esclusivamente per aggiungere i dati di un prodotto per prodotti già esistenti
	 * nella mappa. Corregge il numero di oggetti ordinati sulla base delle scorte disponibili.
	 * 
	 * @param product
	 */
	public void putProductData(Product product) {
		
		if(!cartProductMap.containsKey(product.getCode())) {
			return;
		}
		
		CartProduct cartProduct = cartProductMap.get(product.getCode());
		cartProduct.setProduct(product);
		
		/*
		 * Correggiamo il numero di oggetti ordinati se questo soverchia il numero di
		 * scorte disponibili per il particolare prodotto.
		 */
		int deal = cartProduct.getDeal();
		int availableUnits = product.getAvailableUnits();
		
		if(deal <= availableUnits) {
			cartProduct.setDeal(deal);
		}
		else {
			cartProduct.setDeal(availableUnits);
		}
	}
	
	public void emptyCart() {
		cartProductMap = new HashMap<Integer, CartProduct>();
	}
	
	public int size() {
		return cartProductMap.size();
	}
	
	public boolean isEmpty() {
		return (cartProductMap.size() == 0);
	}
	
}
