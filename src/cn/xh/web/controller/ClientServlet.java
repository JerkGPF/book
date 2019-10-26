package cn.xh.web.controller;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.xh.domain.Book;
import cn.xh.domain.Favorite;
import cn.xh.domain.User;
import cn.xh.service.ClientService;
import cn.xh.service.impl.ClientServiceImpl;
import cn.xh.web.formbean.Cart;
import cn.xh.web.formbean.CartItem;

@WebServlet("/client/ClientServlet")
public class ClientServlet extends HttpServlet {
	private ClientService service = new ClientServiceImpl();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html;charset=UTF-8");
		String op = req.getParameter("op");// �õ�������������
		if (op != null && !op.equals("")) {
			// ��¼
			if (op.equals("login")) {
				login(req, resp);
			}
			// ע��
			if (op.equals("layout")) {
				layout(req, resp);
			}
			// ע��
			if (op.equals("register")) {
				register(req, resp);
			}
			// ��ѧ�������鼮�б�
			if (op.equals("wxys")) {
				wxys(req, resp);
			}
			// ����������鼮�б�
			if (op.equals("rwsk")) {
				rwsk(req, resp);
			}
			// �ٶ�ͯ�����鼮�б�
			if (op.equals("sets")) {
				sets(req, resp);
			}
			// �����������鼮
			if (op.equals("jyks")) {
				jyks(req, resp);
			}
			// ���ý������鼮
			if (op.equals("jjjr")) {
				jjjr(req, resp);
			}
			// ��ѧ�������鼮
			if (op.equals("kxjs")) {
				kxjs(req, resp);
			}
			// ������Ϣ�޸�
			if (op.equals("personInformation")) {
				personInformation(req, resp);
			}
			// �޸�����
			if (op.equals("personPassword")) {
				personPassword(req, resp);
			}
			// ������
			if (op.equals("search")) {
				search(req, resp);
			}
			// ����ҳ��
			if (op.equals("particulars")) {
				particulars(req, resp);
			}
			// ���ӹ��ﳵ
			if (op.equals("addCart")) {
				addCart(req, resp);
			}
			// ɾ�����ﳵ�еĹ�����
			if (op.equals("delItem")) {
				delItem(req, resp);
			}
			// �޸Ĺ���������
			if (op.equals("changeNum")) {
				changeNum(req, resp);
			}
			// �����ղؼ�
			if (op.equals("addfavorite")) {
				addfavorite(req, resp);
			}
			// ��ʾ�ղؼ�
			if (op.equals("showfavorite")) {
				showfavorite(req, resp);
			}
			// ɾ���ղؼ�
			if (op.equals("delFavorite")) {
				delFavorite(req, resp);
			}

		}
	}

	private void delFavorite(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		String book_id = req.getParameter("book_id");
		service.delFavorite(book_id);
		HttpSession session = req.getSession();
		List<Favorite> lists = (List<Favorite>) session.getAttribute("favorite");
		Iterator<Favorite> iterator = lists.iterator();
		while (iterator.hasNext()) {
			Favorite favorite = iterator.next();
			if (book_id.equals(favorite.getBook().getBook_id())) {
				iterator.remove();// ʹ�õ�������ɾ������ɾ��
			}
		}
		resp.sendRedirect(req.getContextPath() + "/favorite.jsp");
	}

	private void showfavorite(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		User user = (User) session.getAttribute("user");
		List<Favorite> favorites = service.findFavoriteByUserId(user);
		session.setAttribute("favorite", favorites);
		req.getRequestDispatcher("/favorite.jsp").forward(req, resp);
	}

	private void addfavorite(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		HttpSession session = req.getSession();
		User user = (User) session.getAttribute("user");
		String user_id = user.getId();
		String book_id = req.getParameter("book_id");
		boolean isExit = service.findFavorite(user_id, book_id);
		if (isExit == false) {
			service.addfavorite(user_id, book_id);
		}
	}

	private void changeNum(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String num = req.getParameter("num");
		String book_id = req.getParameter("book_id");
		// ȡ�����ﳵ
		Cart cart = (Cart) req.getSession().getAttribute("cart");
		CartItem item = cart.getItmes().get(book_id);
		item.setQuantity(Integer.parseInt(num));
		resp.sendRedirect(req.getContextPath() + "/showCart.jsp");

	}

	private void login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		HttpSession session = req.getSession();
		User user = service.login(username, password);
		if (user.getUsername() != null && user.getUsername() != "") {
			req.setAttribute("message", "��½�ɹ�");
			session.setAttribute("user", user);
			req.getRequestDispatcher("/message.jsp").forward(req, resp);
		} else {
			req.setAttribute("message", "�û�����������������µ�¼");
			req.getRequestDispatcher("/message.jsp").forward(req, resp);
		}
	}

	private void layout(HttpServletRequest req, HttpServletResponse resp) {
		try {
			HttpSession session = req.getSession();
			session.removeAttribute("user");// ��ȡsession���󣬴�session���Ƴ���½��Ϣ
			resp.sendRedirect("http://localhost:8080/book/index.jsp");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void register(HttpServletRequest req, HttpServletResponse resp) {
		try {
			String username = req.getParameter("username");
			String password = req.getParameter("password");
			String name = req.getParameter("name");
			String sex = req.getParameter("sex");
			String tel = req.getParameter("tel");
			String address = req.getParameter("address");

			boolean isExist = false;// �ж��Ƿ���ڸ��û�

			if (!username.equals("") && !password.equals("")) {
				isExist = service.register(username, password, name, sex, tel, address);
				if (isExist == true) {
					resp.getWriter().write("���û��Ѿ�ע�ᣬ��ֱ��");
					resp.getWriter().write("<a href='" + req.getContextPath() + "/index.jsp'>��¼</a>");
				} else {
					resp.getWriter().write("ע��ɹ�!");
					resp.getWriter().write("2s��������¼ҳ");
					resp.setHeader("Refresh", "2;URL=" + req.getContextPath() + "/index.jsp");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void wxys(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<Book> books = service.xsys();// ��ѧ�������鼮
		req.setAttribute("books", books);
		req.getRequestDispatcher("/showBook.jsp").forward(req, resp);
	}

	private void rwsk(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<Book> books = service.rwsk();// ����������鼮
		req.setAttribute("books", books);
		req.getRequestDispatcher("/showBook.jsp").forward(req, resp);
	}

	private void sets(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<Book> books = service.sets();// �ٶ�ͯ�����鼮
		req.setAttribute("books", books);
		req.getRequestDispatcher("/showBook.jsp").forward(req, resp);
	}

	private void jyks(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<Book> books = service.jyks();// �����������鼮
		req.setAttribute("books", books);
		req.getRequestDispatcher("/showBook.jsp").forward(req, resp);
	}

	private void jjjr(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<Book> books = service.jjjr();// ���ھ������鼮
		req.setAttribute("books", books);
		req.getRequestDispatcher("/showBook.jsp").forward(req, resp);
	}

	private void kxjs(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<Book> books = service.kxjs();// ��ѧ�������鼮
		req.setAttribute("books", books);
		req.getRequestDispatcher("/showBook.jsp").forward(req, resp);
	}

	private void personInformation(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String username = req.getParameter("username");
		String name = req.getParameter("name");
		String sex = req.getParameter("sex");
		String tel = req.getParameter("tel");
		String address = req.getParameter("address");

		service.personInformation(username, name, sex, tel, address);
		resp.getWriter().write("<div style='text-align: center;margin-top: 260px'><img src='" + req.getContextPath()
				+ "/img/duigou.png'/>�޸ĳɹ���</div>");
	}

	private void personPassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		String repassword = req.getParameter("repassword");

		service.personPassword(password, username);
		resp.getWriter().write("<div style='text-align: center;margin-top: 260px'><img src='" + req.getContextPath()
				+ "/img/duigou.png'/>�޸ĳɹ���</div>");
	}

	private void search(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String search = req.getParameter("search");
		List<Book> searchmessage = service.search(search);
		req.setAttribute("books", searchmessage);
		req.getRequestDispatcher("/showBook.jsp").forward(req, resp);
	}

	private void particulars(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String book_id = req.getParameter("book_id");
		Book book = findBookById(book_id);
		req.setAttribute("book", book);
		req.getRequestDispatcher("/particulars.jsp").forward(req, resp);
	}

	// ͨ���鼮id�ҵ��鼮��Ϣ
	private Book findBookById(String book_id) {
		Book book = service.findBookById(book_id);
		return book;
	}

	private void addCart(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String book_id = req.getParameter("book_id");
		Book book = findBookById(book_id);

		HttpSession session = req.getSession();
		Cart cart = (Cart) session.getAttribute("cart");
		if (cart == null) {
			cart = new Cart();
			session.setAttribute("cart", cart);
		}
		cart.addBook(book);
	}

	private void delItem(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String book_id = req.getParameter("book_id");
		Cart cart = (Cart) req.getSession().getAttribute("cart");
		cart.getItmes().remove(book_id);
		resp.sendRedirect(req.getContextPath() + "/showCart.jsp");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
}