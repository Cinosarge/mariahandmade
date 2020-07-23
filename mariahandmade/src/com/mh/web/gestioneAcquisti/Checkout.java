package com.mh.web.gestioneAcquisti;

import java.io.*;
import java.math.BigDecimal;
import java.sql.SQLException;

import javax.servlet.*;
import javax.servlet.http.*;

import com.mh.model.gestioneAcquisti.DBOrder;
import com.mh.model.javaBeans.Cart;
import com.mh.model.javaBeans.Order;
import com.mh.model.javaBeans.Payment;
import com.mh.model.javaBeans.Purchase;
import com.mh.model.javaBeans.RegisteredUser;

/**
 * 
 * Il checkout è il processo di creazione di un ordine. Si divide in quattro fasi
 * 
 * I. RIEPILOGO: permette di visualizzare gli elementi presenti nel carrello insieme alla quantità ordinata,
 * tenendo conto delle scorte disponibili, il prezzo attuale al momento dell'acquisto e il totale dell'ordine.
 * 
 * II. SPEDIZIONE: permette di selezionare un indirizzo per la spedizione e il tipo di spedizione preferita.
 * 
 * III. PAGAMENTO: permette la selezione di un metodo di pagamento e il pagamento stesso.
 * 
 * IV. COMPLETATO: visualizza le informazioni dell'ordine appena effettuato e permette di tornare
 * alla navigazione dei prodotti sul sito web.
 * 
 * Ad ogni fase corrisponde una pagina <em>.jsp</em> ma l'operazione di checkout va considerata come un'operazione
 * atomica e il componente responsabile per l'intero <em>processo di checkout</em> è questo servlet.
 * 
 * @author Enrico Sbrighi
 * 
 */
public class Checkout extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private final static int BEGIN = 0;
	private final static int SUMMARY = 1;
	private final static int SHIPMENT = 2;
	private final static int PAYMENT = 3;
	private final static int COMPLETED = 4;

	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		String stage = (String) request.getAttribute("stage");
		
		if(stage == null || stage.equals("1")) {
			doPost(request, response);
		}
		else {
			response.setContentType("text/html");
			response.sendError(403);
		}
	}
	
	/**
	 * Non si può raggiungere uno stage di checkout successivo se quello precedente non è completo.
	 * Tuttavia è possibile tornare indietro negli stage ed aggiornare i dati.
	 * 
	 * Viene quindi mantenuta una variabile di sessione che memorizza lo stage 'n' del checkout al
	 * quale si è arrivati e per il quale l'utente dovrà fornire i dati. Si suppone che l'utente
	 * abbia già fornito dati per tutti gli stage nell'intervallo [BEGIN , (n-1)]. Pertanto l'utente
	 * può comunque tornare indietro e fornire informazioni diverse su uno stage precedente ma non
	 * può mai saltare uno stage per andare al successivo.
	 * 
	 * L'utente invia in automatico, insieme alla richiesta POST, un attributo 'stage' con l'informazione
	 * su quale stage del checkout vuole raggiungere. Lo stage richiesto viene verificato confrontandolo
	 * con l'informazione sullo stage mantenuta nella sessione.
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		HttpSession session = request.getSession(false);
		
		/*
		 *  La sessione è scaduta durante il checkout (dopo la scadenza l'utente
		 *  potrebbe aver cercato di aggiungere un prodotto a un carrello oramai
		 *  già vuoto) per poi completare il checkout.
		 */
		if(session == null || session.getAttribute("userData") == null) {
			response.sendRedirect("/mariahandmade/");
			return;
		}
		
		/*
		 * Il carrello è vuoto! Il pulsante di Checkout dovrebbe essere disabilitato
		 * quando il carrello è vuoto e quindi questa circostanza non dovrbbe mai
		 * verificarsi.
		 */
		Cart cart = (Cart) session.getAttribute("cart");
		if(cart == null || cart.isEmpty()) {
			response.sendRedirect("/mariahandmade/");
			return;
		}
		
		// Mantiene traccia dello step del checkout cui si è arrivati
		Integer sessionStage = (Integer) session.getAttribute("stage");
		
		if(sessionStage == null) {
			session.setAttribute("stage", BEGIN);
			sessionStage = BEGIN;
		}
		
		// Maniete traccia dello step richiesto dal client
		int requestStage = BEGIN;
		
		String requestStageParameter = request.getParameter("stage");
		if(requestStageParameter != null) {
			requestStage = Integer.parseInt(requestStageParameter);
		}
		
		/*
		 * L'attributo di sessione 'stage' può cambiare verso lo stage successivo se i dati ricevuti
		 * sono corretti e arrivano da una richiesta con un parametro 'stage' minore  uguale a esso.
		 * 
		 * Se lo stage indicato nella  richiesta è precedente a quello indicato nella sessione i dati
		 * vengono aggiornati e si procede allo stage successivo.
		 * 
		 * Se lo stage indicato nella richiesta è successivo a quello di sessione si è in presenza di un
		 * tentativo di frode.
		 * 
		 */
		if(requestStage >= BEGIN && requestStage <= sessionStage) {
			
			Order order = null;
			
			switch(requestStage) {
				case BEGIN:
					session.setAttribute("stage", SUMMARY);
					
					RequestDispatcher summaryView = request.getRequestDispatcher("CheckoutSummary.jsp");
					summaryView.forward(request, response);
					break;
				case SUMMARY:
					order = makeOrder(request);
					
					session.setAttribute("order", order);
					session.setAttribute("stage", SHIPMENT);
					
					RequestDispatcher shipmentView = request.getRequestDispatcher("CheckoutShipment.jsp");
					shipmentView.forward(request, response);
					break;
				case SHIPMENT:
					order = addShipmentInfo(request);
					
					session.setAttribute("order", order);
					session.setAttribute("stage", PAYMENT);
					
					RequestDispatcher paymentView = request.getRequestDispatcher("CheckoutPayment.jsp");
					paymentView.forward(request, response);
					break;
				case PAYMENT:
					order = addPaymentInfo(request);
					
					session.setAttribute("order", order);			
					session.setAttribute("stage", COMPLETED);
					
					DBOrder orderAPI = new DBOrder();
					
					try {
						orderAPI.insertOrder(order);
						
						/*
						 * Se l'inserimento dell'ordine va ad buon vine
						 * svuotiamo il carrello.
						 */
						cart.emptyCart(); 
					}
					catch(SQLException e) {
						response.sendError(500);
						System.out.println(e.getClass().getName() + ": " + e.getMessage());
						return;
					}
					
					RequestDispatcher completedView = request.getRequestDispatcher("CheckoutCompleted.jsp");
					completedView.forward(request, response);
					break;
				case COMPLETED:
					// Not much to do here
					break;
				default:
					response.sendError(500);
			}
			
		}
		else {
			response.sendError(403);
		}
		
	}
	
	/*
	 * Preleva i parametri della richiesta HTTP POST e crea un bean di tipo Order
	 * Si aspettano i seguenti parametri dalla richiesta per ogni prodotto nel carrello:
	 * 
	 * - productCode
	 * - deal
	 */
	private Order makeOrder(HttpServletRequest request) {
		Order order = new Order();
		
		Purchase purchase = null;
		String [] productCode = request.getParameterValues("productCode");
		String [] deal = request.getParameterValues("deal");
		
		HttpSession session = request.getSession(false);
		Cart cart = (Cart) session.getAttribute("cart");
		double purchaseTotal = 0.0;
		
		for(int i = 0; i < productCode.length; i++) {
			purchase = new Purchase();
			
			int code = Integer.parseInt(productCode[i]);
			purchase.setProductCode(code);
			purchase.setProduct(cart.getCartProductMap().get(code).getProduct());
			purchase.setOriginalPrice(cart.getCartProductMap().get(code).getProduct().getPrice());
			
			int purchaseDeal = Integer.parseInt(deal[i]);
			purchase.setUnits(purchaseDeal);
			
			purchaseTotal += purchaseDeal * cart.getCartProductMap().get(code).getProduct().getPrice().doubleValue();
			
			order.getPurchaseList().add(purchase);
		}
		
		order.setTotal(BigDecimal.valueOf(purchaseTotal));
		
		RegisteredUser user = (RegisteredUser) session.getAttribute("userData");
		order.setUserID(user.getUserID());
		
		return order;
	}
	
	/*
	 * Preleva i parametri della richiesta HTTP POST e aggiorna il bean di tipo Order
	 * Si aspettano i seguenti parametri dalla richiesta:
	 * 
	 * - addressID
	 */
	private Order addShipmentInfo(HttpServletRequest request) {
		HttpSession session = request.getSession();
		Order order = (Order) session.getAttribute("order");
		
		int shipmentAddressID = Integer.parseInt(request.getParameter("addressID"));
		order.setAddressID(shipmentAddressID);
		
		return order;
	}
	
	/*
	 * Preleva i parametri della richiesta HTTP POST e aggiorna il bean di tipo Order
	 * Si aspettano i seguenti parametri dalla richiesta:
	 * 
	 * - paymentType
	 */
	private Order addPaymentInfo(HttpServletRequest request) {
		HttpSession session = request.getSession();
		Order order = (Order) session.getAttribute("order");
		
		Payment payment = new Payment();
		
		RegisteredUser user = (RegisteredUser) session.getAttribute("userData");
		payment.setUserID(user.getUserID());
		
		String paymentType = request.getParameter("paymentType");
		payment.setPaymentType(paymentType);
		
		/*
		 * Nel nostro semplice caso il pagamento risulta già effettuato
		 */
		payment.setPaymentState("PAYED");
		
		order.setPayment(payment);
		
		return order;
	}
}
