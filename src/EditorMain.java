/**
 * 
 * 如何设置使得messagePointer的显示能始终靠左
 * messagePointer设置的光标所在行列数不精确
 * 
 */

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Choice;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.BadLocationException;

public class EditorMain {
	JFrame mainFrame;			//主窗口
	
	//字体：
	String[] localFont=GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
	
	//默认系统字体
	Font initFontSys=new Font("Arial",Font.PLAIN,18);
	//默认编辑字体
	Font initFontEdit=new Font("新宋体",Font.PLAIN,20);
	//剪切板
	Clipboard cutpad=Toolkit.getDefaultToolkit().getSystemClipboard();
	
	JTextArea ta;		   //文字写入区
	JScrollPane jsp;
	
	JMenuBar fileMenu;	   //工具条
	
	JMenu file;		    	//文件
	JMenuItem openFile;      //打开文件
	JMenuItem saveFile;	    //保存文件
	JMenuItem exit;		    //退出
	JMenuItem close;		    //关闭当前文档
	
	JMenu edit;			//编辑
	JMenuItem cut;		//剪切
	JMenuItem copy;		//复制
	JMenuItem paste;		//粘贴
	JMenuItem delete;	//删除
	JMenuItem selectAll; //全选
	JMenuItem find;		//查找
	JMenuItem replace;   //替换
	JMenu insert;		//插入
	JMenuItem date;		//日期
	JMenuItem time;		//时间
	JCheckBoxMenuItem readonly;  //只读
	JCheckBoxMenuItem onTop;		//置顶
	
	
	JMenu set;			//设置
	JMenuItem font;      //字体设置
	JMenuItem view;      //外观设置
	JMenu code;          //编码设置
	JCheckBoxMenuItem autowrap;  //自动换行设置
	//文件编辑类型
	ButtonGroup codeType;
	JRadioButtonMenuItem utf8;      
	JRadioButtonMenuItem utf16;
	JRadioButtonMenuItem unicode;
	JRadioButtonMenuItem gbk;
	JRadioButtonMenuItem gb2312;
	JRadioButtonMenuItem ascii;
	JRadioButtonMenuItem iso88591;
	
	JMenu help;			//帮助
	JMenuItem use;       //使用
	JMenuItem about;     //关于
	
	JPanel lowPanel;		    //底边栏
	JLabel messagePointer;      //底边栏消息：鼠标位置
	JLabel messageText;	      //底边栏消息：文件大小
	JLabel messageCoding;      //底边栏消息：编码方式
	JLabel messageType;	      //底边栏消息：编码类型
	
	//要操作的文件
	File mainFile;
	public EditorMain(){
		init();
	}
	private void init(){
		//初始化主窗口
		mainFrame=new JFrame("file");
		mainFrame.setBounds(320, 110, 850, 600);
		mainFrame.setFont(initFontSys);
		
		//添加主文本区
		ta=new JTextArea();
		jsp=new JScrollPane();
		jsp.setViewportView(ta);
		ta.setFont(initFontEdit);
		jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		mainFrame.add(jsp);
		
		//添加菜单栏
		menuBarInit();
		
		//添加底边栏：
		lowPanelInit();
		
		//响应事件：
		//主事件：
		myEvent();
		
		//快捷键设置：
		shotcutInit();
	
		mainFrame.setVisible(true);
		
	}
	private void shotcutInit(){
		openFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,InputEvent.CTRL_MASK));
		saveFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_MASK));
		exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,InputEvent.CTRL_DOWN_MASK));
		cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,InputEvent.CTRL_MASK));
		copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,InputEvent.CTRL_MASK));
		paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,InputEvent.CTRL_MASK));
		delete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,InputEvent.CTRL_MASK));
		selectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,InputEvent.CTRL_MASK));
		find.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,InputEvent.CTRL_MASK));
		replace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,InputEvent.CTRL_MASK));
		
		ta.addKeyListener(new KeyAdapter(){
			
			@Override
			public void keyTyped(KeyEvent e) {
					
					System.out.println(e.getKeyChar());
					Font f=ta.getFont();
					ta.setFont(new Font(f.getName(),f.getStyle(),f.getSize()+1));
			}
			
		});
	}
	private void menuBarInit(){
		//创建工具栏
		file=new JMenu("file");
		openFile=new JMenuItem("open");
		saveFile=new JMenuItem("save");
		close=new JMenuItem("close");
		exit=new JMenuItem("exit");
		file.add(openFile);
		file.add(saveFile);
		file.addSeparator();
		file.add(close);
		file.add(exit);
		//创建编辑栏
		edit=new JMenu("edit");
		cut=new JMenuItem("cut");
		cut.setEnabled(false);
		copy=new JMenuItem("copy");
		copy.setEnabled(false);
		paste=new JMenuItem("paste");
		delete=new JMenuItem("delete");
		selectAll=new JMenuItem("selectAll");
		find=new JMenuItem("find");
		replace=new JMenuItem("replace");
		insert=new JMenu("insert");
		date=new JMenuItem("date");
		time=new JMenuItem("time");
		readonly=new JCheckBoxMenuItem("readonly");
		onTop=new JCheckBoxMenuItem("onTop");
		
		edit.add(cut);
		edit.add(copy);
		edit.add(paste);
		edit.add(delete);
		edit.addSeparator();
		edit.add(selectAll);
		edit.add(find);
		edit.add(replace);
		edit.add(insert);
		insert.add(date);
		insert.add(time);
		edit.addSeparator();
		edit.add(readonly);
		edit.add(onTop);
		//创建设置栏
		set=new JMenu("set");
		font=new JMenuItem("font");
		view=new JMenuItem("view");
		code=new JMenu("code");
		autowrap=new JCheckBoxMenuItem("autoWrap");
		set.add(font);
		set.add(view);
		set.add(code);
		set.add(autowrap);
		
		codeType=new ButtonGroup();
		utf8=new JRadioButtonMenuItem("utf-8",false);
		utf16=new JRadioButtonMenuItem("utf-16",false);
		gb2312=new JRadioButtonMenuItem("gb2312",false);
		gbk=new JRadioButtonMenuItem("gbk",true);
		iso88591=new JRadioButtonMenuItem("iso-8859-1",false);
		ascii=new JRadioButtonMenuItem("ascii",false);
		codeType.add(utf8);
		codeType.add(utf16);
		codeType.add(gb2312);
		codeType.add(gbk);
		codeType.add(iso88591);
		codeType.add(ascii);
		code.add(gbk);
		code.add(utf8);
		code.add(gb2312);
		code.add(utf16);
		code.add(ascii);
		code.add(iso88591);
		
		//设置帮助栏
		help=new JMenu("help");
		use=new JMenuItem("using");
		about=new JMenuItem("about");
		help.add(use);
		help.add(about);
		
		fileMenu=new JMenuBar();
		fileMenu.add(file);
		fileMenu.add(edit);
		fileMenu.add(set);
		fileMenu.add(help);
		mainFrame.setJMenuBar(fileMenu);
	}
	private void lowPanelInit(){
		lowPanel =new JPanel();
		lowPanel.setLayout(new FlowLayout(FlowLayout.TRAILING,10,0));
		lowPanel.setBackground(new Color(0xFFFFCC));
		
		messagePointer=new JLabel("0000:0000");
		messageText=new JLabel("----------------");
		messageCoding=new JLabel("GBK");
		messageType=new JLabel("plain");
		
		lowPanel.add(messagePointer);
		lowPanel.add(new JLabel("                                              "));
		lowPanel.add(new JLabel("                                              "));
		lowPanel.add(messageText);
		lowPanel.add(new JLabel("                     "));
		lowPanel.add(messageCoding);
		lowPanel.add(new JLabel("|"));
		lowPanel.add(messageType);
		lowPanel.add(new JLabel("                      "));
		
		mainFrame.add(lowPanel, "South");
	}
	private void myEvent(){
		
		edit.addActionListener(new exitClosingAction(){
			public void actionPerformed(ActionEvent e){
				if(cutpad.isDataFlavorAvailable(DataFlavor.stringFlavor)){
					paste.setEnabled(true);
				}
				else{
					paste.setEnabled(false);
				}
			}
		});
		
		//给底边栏的光标显示信息注册监听器
		ta.addKeyListener(new KeyAdapter(){
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					Rectangle r=ta.modelToView(ta.getCaretPosition());
					
					int sleft=r.y/r.height+1;
					int sright=(r.x/r.width)/7+1;
					messagePointer.setText(sleft+":"+sright);
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}	
			}
		});
		ta.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				try {
					Rectangle r=ta.modelToView(ta.getCaretPosition());
					int sleft=r.y/r.height+1;
					int sright=(r.x/r.width)/7+1;
					messagePointer.setText(sleft+":"+sright);
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
			}
		});
		//help工具条事件注册
		use.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				JDialog used=new JDialog(mainFrame,"use mothod");
				used.setLayout(new GridLayout(2,1,5,5));
				used.setLocation(550,250);
				used.add(new JLabel("<html>"
						+ " <h3>                   thank you for using!\n"+"</html>"));
				used.add(new JLabel("<html>"
						+ "<h3>get more helping,please click <a href='http://www.baidu.com;'>hear        </a>"
						+ "</hmtl>"));
				used.pack();
				used.setVisible(true);
			}
		});
		about.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				JDialog aboutd=new JDialog(mainFrame,"message");
				aboutd.setLayout(new GridLayout(3,1,10,0));
				aboutd.setBounds(550,250,200,150);
				aboutd.add(new JLabel("<html>"
						+ " <h2>--author:"+"<strong,style='color:purple;font-style:italic;'>  dtdyq"+"</html>"));
				aboutd.add(new JLabel("<html>"
						+ " <h2>--verson:"+"<strong style='color:purple;font-style:italic;'>   1.0"+"</html>"));
				aboutd.add(new JLabel("<html>"
						+ " <h2>--time :"+"<strong style='color:purple;font-style:italic;'> 17-1-27"+"</html>"));
				aboutd.setVisible(true);
			}
		});
		//文件菜单事件注册
		fileMenuEvent();
		//编辑菜单事件注册
		editMenuEvent();
		//设置菜单事件注册
		setMenuEvent();
	}
	private void fileMenuEvent(){
		//给打开按钮注册监听器
		openFile.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				openFile();
			}
		});
		//给保存按钮注册监听器
		saveFile.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){

				if(mainFile==null){
					JFileChooser fdsave=new JFileChooser(new File("C:/Users/dtdyq/Desktop/"));
					fdsave.setBounds(300,800,500,450);
					int returnval=fdsave.showSaveDialog(mainFrame);
					fdsave.setVisible(true);
					if(returnval==JFileChooser.APPROVE_OPTION){
						mainFile=fdsave.getSelectedFile();
						saveFile();
					}
					else{
						fdsave.setVisible(false);
					}
				}
				else{
					saveFile();
				}
			}
		});
		//给exit注册监听器
		exit.addActionListener(new exitClosingAction());
		//给close注册监听器
		close.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(mainFile!=null){
					try{
						BufferedWriter br=new BufferedWriter(new FileWriter(mainFile));
						br.write(ta.getText());
						br.flush();
						br.close();
						messageText.setText("0Mb0Kb");
						messageType.setText("plain");
					}catch(IOException ee){
						ee.printStackTrace();
					}
				}
				ta.setText("");
			}
		});
	}
	private void setMenuEvent(){
		//设置字体
		font.addActionListener(new fontSetAction());
		//设置外观
		view.addActionListener(new viewSetAction());
		//设置自动换行
		autowrap.addItemListener(new ItemListener(){

			@Override
			public void itemStateChanged(ItemEvent e) {
				if(autowrap.isSelected()){
					ta.setLineWrap(true);
				}
				else{
					ta.setLineWrap(false);
				}
			}
		});
		Enumeration<AbstractButton> codetype=codeType.getElements();
		while(codetype.hasMoreElements()){
			AbstractButton cp=codetype.nextElement();
			cp.addItemListener(new ItemListener(){

				@Override
				public void itemStateChanged(ItemEvent e) {
					if(cp.isSelected()){
						messageCoding.setText(cp.getText());
					}
					else{
						return;
					}
				}
				
			});
		}
	}
	private void editMenuEvent(){
		ta.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseReleased(MouseEvent e) {
				if(!"".equals(ta.getSelectedText())){
					cut.setEnabled(true);
					copy.setEnabled(true);
				}
			}
		});
		cut.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				StringSelection str=new StringSelection(ta.getSelectedText());
				cutpad.setContents(str, null); 
				
				ta.replaceRange("", ta.getSelectionStart(),ta.getSelectionEnd());
			}
		});
		copy.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				StringSelection str=new StringSelection(ta.getSelectedText());
				cutpad.setContents(str, null);
			}
		});
		paste.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(!cutpad.isDataFlavorAvailable(DataFlavor.stringFlavor)){
					return;
				}
				String content=null;
				try {
					content = (String)cutpad.getData(DataFlavor.stringFlavor);
				} catch (UnsupportedFlavorException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				ta.insert(content,ta.getCaretPosition());
			}
		});
		delete.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				ta.setText("");
			}
		});
		selectAll.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				ta.selectAll();
			}
		});
		find.addActionListener(new editFindAction());
		date.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				GregorianCalendar  c=new GregorianCalendar();
				ta.insert(String.format("%1$tb-%1$te-%1$tY", c), ta.getCaretPosition());
			}
		});
		time.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				ta.insert(String.format("%tT", Calendar.getInstance()), ta.getCaretPosition());
			}
		});
		readonly.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(readonly.getState()){
					ta.setEnabled(false);
				}
				else{
					ta.setEnabled(true);
				}
			}
		});
		onTop.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(onTop.getState()){
					mainFrame.setAlwaysOnTop(true);
				}
				else{
					mainFrame.setAlwaysOnTop(false);
				}
			}
		});
		replace.addActionListener(new editReplaceAction());
	}

	private void saveFile(){
		
		try{
			mainFile.createNewFile();
			mainFrame.setTitle(mainFile+"");
			OutputStreamWriter or=new OutputStreamWriter(new FileOutputStream(mainFile),messageCoding.getText());
			BufferedWriter bw=new BufferedWriter(or);
			String str=ta.getText();
			
			String[] temp=mainFile.getName().split("\\.");
			messageType.setText(temp[temp.length-1]);
		
			int len=str.length();
			if(len>1024*1024){
				messageText.setText(String.format("%3.2f", (float)len/(1024*1024))+"Mb");
			}
			else{
				messageText.setText(String.format("%5.2f", (float)len/1024)+"Kb");
			}
			
			bw.write(str);
			bw.flush();
			bw.close();
		}catch(IOException ee){
			ee.printStackTrace();
		}
	}
	private void openFile(){
		JFileChooser fdopen=new JFileChooser(new File("C:/Users/dtdyq/Desktop/"));
		int returnval=fdopen.showOpenDialog(mainFrame);
		
		fdopen.setBounds(300,800,500,450);
		fdopen.setVisible(true);

		ta.setText("");
		if(returnval==JFileChooser.APPROVE_OPTION){
			File openfile=fdopen.getSelectedFile();
			mainFile =openfile;
			mainFrame.setTitle(openfile+"");
			try{
				InputStreamReader ir=new InputStreamReader(new FileInputStream(mainFile),messageCoding.getText());
				BufferedReader br=new BufferedReader(ir);

				String[] temp=mainFile.getName().split("\\.");
				messageType.setText(temp[temp.length-1]);
				
				StringBuilder sb=new StringBuilder();
				String str;
				while((str=br.readLine())!=null){
					sb.append(str+"\r\n");
				}
				
				int len=sb.length();
				if(len>1024*1024){
					messageText.setText(String.format("%3.2f", (float)len/(1024*1024))+"Mb");
				}
				else{
					messageText.setText(String.format("%5.2f", (float)len/1024)+"Kb");
				}
				
				ta.append(sb.toString());
				ta.setCaretPosition(0);
				br.close();
			}catch(IOException ee){
				ee.printStackTrace();
			}
		}
		else{
			fdopen.setVisible(false);
		}
		
	}
	
	
	public static void main(String[] args){
		new EditorMain();
	}
	

	private class exitClosingAction implements ActionListener{
		JDialog message;
		JButton bsave;
		JButton bnotSave;
		JButton bcancel;
		JLabel tipl;
		public void actionPerformed(ActionEvent e){
			//初始化：要在响应对应的事件的方法中初始化，而不能再类初始化时初始化！！！
			//否则第二次打开对话框时将不再显示其中的组件
			message=new JDialog(mainFrame,"tip!");
			bsave=new JButton("save");
			bnotSave=new JButton("not save");
			bcancel=new JButton("cancel");
			tipl=new JLabel("      file has been changed,save or not?");
			
			if( mainFile==null&&ta.getText().equals("")){
				System.exit(0);
			}
			else if( mainFile==null&&!ta.getText().equals("")){
				init();
			}
			else if(mainFile!=null){
				BufferedReader br;
				StringBuilder fileText=new StringBuilder();
				try{
					br=new BufferedReader(new FileReader(mainFile));
					String temp;
					while((temp=br.readLine())!=null){
						fileText.append(temp+"\r\n");
					}
					br.close();
				}catch(IOException ee){
					ee.printStackTrace();
				}
				if(ta.getText().equals(fileText.toString())){
					System.exit(0);
				}
				init();
			}
		}
		private void init(){
			JPanel p=new JPanel(new FlowLayout(FlowLayout.RIGHT));
			p.add(bsave);
			p.add(bnotSave);
			p.add(bcancel);
			
			message.add(tipl);
			message.add(p,"South");
			message.setBounds(450,250, 300,150);
			thisEvent();
			message.setVisible(true);
		}
		private void thisEvent(){
			bsave.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e){
					if(mainFile!=null){
						saveFile();
						System.exit(0);
					}
					
					JFileChooser fdsave=new JFileChooser(new File("C:/Users/dtdyq/Desktop/"));
					fdsave.setDialogType(JFileChooser.SAVE_DIALOG);
					fdsave.setApproveButtonText("save");
					int returnval=fdsave.showSaveDialog(mainFrame);

					fdsave.setSize(500,450);
					fdsave.setVisible(true);
					
					if(returnval==JFileChooser.APPROVE_OPTION){
						File savefile=fdsave.getSelectedFile();
						System.out.println(savefile);
						try {
							savefile.createNewFile();
							BufferedWriter bw=new BufferedWriter(
									new OutputStreamWriter(
											new FileOutputStream(savefile),messageCoding.getText()
											)
									);
							bw.write(ta.getText());
							
							bw.close();
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						} catch (UnsupportedEncodingException e1) {
							e1.printStackTrace();
						}catch(IOException e3){
							e3.printStackTrace();
						}
						System.exit(0);
					}
					else{
						fdsave.setVisible(false);
					}
				}
			});
			bcancel.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					message.setVisible(false);
				}
			});
			bnotSave.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					System.exit(0);
				}
			});
		}
	}
	private class editFindAction implements ActionListener{
		JDialog findText;
		JTextField findNeed;
		JRadioButton up;
		JRadioButton down;
		JButton  next;
		JButton cancel;
		
		LinkedList<Integer> pointer;
		
		public void actionPerformed(ActionEvent e){
			findText=new JDialog(mainFrame,"find");
			findText.setBounds(600,200,290,120);
			findText.setLayout(new GridLayout(1,2,2,2));
			
			findNeed=new JTextField(11);
			JPanel left=new JPanel(new FlowLayout(1,1,8));
			left.add(findNeed);
			
			ButtonGroup bg=new ButtonGroup();
			down=new JRadioButton("down",false);
			up=new JRadioButton("up",false);
			bg.add(down);
			bg.add(up);
			
			JPanel leftdown=new JPanel();
			leftdown.add(down);
			leftdown.add(up);
			left.add(leftdown);
			
			next=new JButton("  next  ");
			JPanel ptemp1=new JPanel();
			ptemp1.add(next);
			cancel=new JButton("cancel");
			JPanel ptemp2=new JPanel();
			ptemp2.add(cancel);
			JPanel right=new JPanel(new GridLayout(2,1,1,5));
			right.add(ptemp1);
			right.add(ptemp2);
			
			findText.add(left);
			findText.add(right);
			findEvent();
			findText.setVisible(true);
		}
		private void findEvent(){
			pointer=new LinkedList<>(); 
			findText.addWindowListener(new WindowAdapter(){
				@Override
				public void windowActivated(WindowEvent e) {
					String textTarget=findNeed.getText();
					String textBasic=ta.getText();
					if("".equals(textTarget)||"".equals(textBasic)){
						return; 
					}
					if(up.isSelected()){
						pointer=TextfindIndexs(textBasic,textTarget);
						Collections.reverse(pointer);
					}
					else if(down.isSelected()){
						pointer=TextfindIndexs(textBasic,textTarget);
					}
				}
			});
			up.addItemListener(new ItemListener(){
				@Override
				public void itemStateChanged(ItemEvent e) {
					if(up.isSelected()){
						String textTarget=findNeed.getText();
						String textBasic=ta.getText();
						if("".equals(textTarget)||"".equals(textBasic)){
							return; 
						}
						pointer=TextfindIndexs(textBasic,textTarget);
						Collections.reverse(pointer);
					}
				}
			});
			down.addItemListener(new ItemListener(){
				@Override
				public void itemStateChanged(ItemEvent e) {
					if(down.isSelected()){
						String textTarget=findNeed.getText();
						String textBasic=ta.getText();
						if("".equals(textTarget)||"".equals(textBasic)){
							return; 
						}
						pointer=TextfindIndexs(textBasic,textTarget);
					}
				}
			});
			next.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					
					if(pointer.isEmpty()){
						JDialog message=new JDialog(findText,"tip!",true);
						message.setLocation(620,220);
						JPanel m=new JPanel();
						JLabel ml=new JLabel(" can not find! ");
						m.add(ml);
						message.add(m);
						message.pack();
						
						message.setVisible(true);
					}
					else{
						int p=pointer.remove();
						int TextLen=findNeed.getText().length();
						ta.select(p, p+TextLen);
					}
				}
			});
			cancel.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					findText.setVisible(false);
				}
			});
		}
		private LinkedList<Integer> TextfindIndexs(String basicText,String targetText){
			LinkedList<Integer> ll=new LinkedList<>();
			Pattern ptemp=Pattern.compile(targetText);
			Matcher mtemp=ptemp.matcher(basicText);
			while(mtemp.find()){
				ll.add(mtemp.start());
			}
			return ll;
		}
	}
	private class fontSetAction implements ActionListener{
		JDialog fontset;
		
		Choice fontType;
		Choice fontColor;
		Choice fontPattern;
		Choice fontSize;
		
		JLabel example;
		JButton queding;
		JButton quxiao;
		
		private HashMap<String,Integer> fontpattern=new HashMap<>();
		private HashMap<String,Color> fontcolor=new HashMap<>();
		private HashMap<Integer,String> fontstyle=new HashMap<>(); 
		
		@Override
		public void actionPerformed(ActionEvent e) {
			//初始化：（好像只能在该函数中初始化，否则第二次打开该dialog对话框时，里面的组件将不会显示）
			fontset=new JDialog(mainFrame,"font",true);
			fontType=new Choice();
			fontPattern=new Choice();
			fontSize=new Choice();
			fontColor=new Choice();
			example=new JLabel("AaBbCcDd");
			queding=new JButton("confirm");
			quxiao=new JButton("cancel");
			
			fontset.setLayout(new BorderLayout());
			fontset.setLocation(550,200);
			
			Font ini=ta.getFont();
			fontType.add(ini.getFamily());
			for(int i=0;i<localFont.length;i++){
				fontType.add(localFont[i]);
			}
			fontSize.add(""+ini.getSize());
			for(int i=2;i<50;i+=2){				
				fontSize.add(""+i);
			}
			fontstyle.put(0, "normal");
			fontstyle.put(1, "bold");
			fontstyle.put(2, "italic");
			fontstyle.put(3, "bold-italic");
			fontPattern.add(fontstyle.get(ini.getStyle())+"");
			fontPattern.add("normal");
			fontPattern.add("bold");
			fontPattern.add("italic");
			fontPattern.add("bold-italic");
			fontpattern.put("normal", Font.PLAIN);
			fontpattern.put("bold", Font.BOLD);
			fontpattern.put("italic", Font.ITALIC);
			fontpattern.put("bold-italic", Font.BOLD+Font.ITALIC);

			fontColor.add("");
			fontColor.add("black");
			fontColor.add("red");
			fontColor.add("blue");
			fontColor.add("green");
			fontColor.add("purple");
			fontColor.add("pink");
			fontColor.add("cyan");
			fontColor.add("brown");
			fontColor.add("white");
			fontcolor.put("black", new Color(0x000000));
			fontcolor.put("red", new Color(0xff0000));
			fontcolor.put("blue", new Color(0x0000ff));
			fontcolor.put("green", new Color(0x00ff00));
			fontcolor.put("purple", new Color(0x800080));
			fontcolor.put("cyan", new Color(0x00ffff));
			fontcolor.put("brown", new Color(0xa52a2a));
			fontcolor.put("while", new Color(0xffffff));
			
			JPanel top=new JPanel(new FlowLayout(4,2,10));
			top.add(fontType);
			top.add(fontPattern);
			top.add(fontSize);
			top.add(fontColor);
			
			JPanel down=new JPanel(new FlowLayout(1,15,15));
			down.add(example);
			down.add(queding);
			down.add(quxiao);
			
			example.setFont(new Font("Arial",Font.PLAIN,20));
			example.setForeground(new Color(0x000000));
			fontset.add(top);
			fontset.add(down,"South");
			fontset.pack();
			fontEventInit();
			fontset.setModal(true);
			fontset.setVisible(true);
		}
		private void fontEventInit(){
			String tempType=fontType.getSelectedItem(); 
			String tempColor=fontColor.getSelectedItem();
			String tempSize=fontSize.getSelectedItem();
			String tempPattern=fontPattern.getSelectedItem();
			fontType.addItemListener(new ItemListener(){

				@Override
				public void itemStateChanged(ItemEvent e) {
					String temp=fontType.getSelectedItem();
					example.setFont(new Font(temp,fontpattern.get(tempPattern),Integer.parseInt(tempSize)));
					example.setForeground(fontcolor.get(tempColor));
					
				}
			});
			fontSize.addItemListener(new ItemListener(){

				@Override
				public void itemStateChanged(ItemEvent e) {
					example.setFont(new Font(fontType.getSelectedItem(),fontpattern.get(fontPattern.getSelectedItem()),Integer.parseInt(fontSize.getSelectedItem())));
					example.setForeground(fontcolor.get(fontColor.getSelectedItem()));
				}
			});
			fontColor.addItemListener(new ItemListener(){

				@Override
				public void itemStateChanged(ItemEvent e) {
					example.setFont(new Font(fontType.getSelectedItem(),fontpattern.get(fontPattern.getSelectedItem()),Integer.parseInt(fontSize.getSelectedItem())));
					example.setForeground(fontcolor.get(fontColor.getSelectedItem()));
				}
			});
			fontPattern.addItemListener(new ItemListener(){

				@Override
				public void itemStateChanged(ItemEvent e) {
					example.setFont(new Font(fontType.getSelectedItem(),fontpattern.get(fontPattern.getSelectedItem()),Integer.parseInt(fontSize.getSelectedItem())));   
					example.setForeground(fontcolor.get(fontColor.getSelectedItem()));
				}
			});
			fontset.addWindowListener(new WindowAdapter(){
				public void windowClosing(WindowEvent e){
					fontset.setVisible(false);
					mainFrame.remove(fontset);
				}
			});
			queding.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e){
					ta.setFont(new Font(fontType.getSelectedItem(),fontpattern.get(fontPattern.getSelectedItem()),Integer.parseInt(fontSize.getSelectedItem())));
					ta.setForeground(fontcolor.get(fontColor.getSelectedItem()));
					fontset.setVisible(false);
				}
			});
			quxiao.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e){
					fontset.setVisible(false);
					fontType.select(tempType);
					fontPattern.select(tempPattern);
					fontSize.select(tempSize);
					fontColor.select(tempColor);
				}
			});
		}
	}
	private class viewSetAction implements ActionListener{
		//主窗口
		JDialog viewset;
		//上边栏按钮
		JPanel north;
		JButton mainFrameView;
		JButton tabgColor;
		JButton lineNumber;
		//中间按钮
		CardLayout viewlayout;
		JPanel midp;
		JPanel mainFrameViewp;
		ButtonGroup styleSelect;
		JPanel tabgColorp;
		ButtonGroup colorSelect;
		JButton morecolor;
		JPanel lineNumberp;
		ButtonGroup lineNumset;
		JTextField selfdefine;
		//下边栏
		JPanel south;
		JButton bcon;
		JButton bcancel;
		
		HashMap<String,String> style=new HashMap<>();
		HashMap<String,Integer> colorSet=new HashMap<>();
		@Override
		public void actionPerformed(ActionEvent e) {
			viewset=new JDialog(mainFrame,"view",true);
			viewset.setLocation(550, 300);
			
			mainFrameView=new JButton("mainStyle");
			tabgColor=new JButton("bgcolor");
			lineNumber=new JButton("lineNumber");
			north=new JPanel();
			north.setLayout(new BoxLayout(north,BoxLayout.X_AXIS));
			north.add(mainFrameView);
			north.add(tabgColor);
			north.add(lineNumber);
			
			viewlayout=new CardLayout();
			midp=new JPanel(viewlayout);
			
			mainFrameViewp=new JPanel();
			mainFrameViewp.setLayout(new GridLayout(5,1,0,5));
			styleSelect=new ButtonGroup();
			JRadioButton Metal=new JRadioButton("  Metal");
			JRadioButton Nimbus=new JRadioButton("  Nimbus");
			JRadioButton Motif=new JRadioButton("  Motif");
			JRadioButton Windows=new JRadioButton("  Windows");
			JRadioButton Classic=new JRadioButton("  Classic");
			styleSelect.add(Metal);
			styleSelect.add(Nimbus);
			styleSelect.add(Motif);
			styleSelect.add(Windows);
			styleSelect.add(Classic);
			mainFrameViewp.add(Metal);
			mainFrameViewp.add(Nimbus);
			mainFrameViewp.add(Motif);
			mainFrameViewp.add(Windows);
			mainFrameViewp.add(Classic);
			
			tabgColorp=new JPanel();
			morecolor=new JButton("more...");
			tabgColorp.setLayout(new GridLayout(6,1,5,5));
			colorSelect=new ButtonGroup();
			JRadioButton white=new JRadioButton("<html><p,style='color:white'>white</p></html>");
			JRadioButton green=new JRadioButton("<html><p,style='color:green'>green</p></html>");
			JRadioButton red=new JRadioButton("<html><p,style='color:red'>red</p></html>");
			JRadioButton gray=new JRadioButton("<html><p,style='color:gray'>gray</p></html>");
			JRadioButton blue=new JRadioButton("<html><p,style='color:blue'>blue</p></html>");
			colorSelect.add(white);
			colorSelect.add(green);
			colorSelect.add(red);
			colorSelect.add(gray);
			colorSelect.add(blue);
			tabgColorp.add(white);
			tabgColorp.add(green);
			tabgColorp.add(red);
			tabgColorp.add(gray);
			tabgColorp.add(blue);
			JPanel p=new JPanel();
			p.add(new JLabel("                  "));
			p.add(morecolor);
			tabgColorp.add(p);
			
			lineNumberp=new JPanel();
			lineNumberp.setLayout(new GridLayout(4,1,5,5));
			lineNumset=new ButtonGroup();
			JRadioButton nonum=new JRadioButton("not set");
			JRadioButton fromzero=new JRadioButton("from zero");
			JRadioButton fromfrist=new JRadioButton("from frist");
			selfdefine=new JTextField(8);
			lineNumset.add(nonum);
			lineNumset.add(fromzero);
			lineNumset.add(fromfrist);
			JPanel jp=new JPanel();
			jp.add(new JLabel("selt define:"));
			jp.add(selfdefine);
			lineNumberp.add(nonum);
			lineNumberp.add(fromzero);
			lineNumberp.add(fromfrist);
			lineNumberp.add(jp);
			
			JPanel p1=new JPanel();
			p1.setLayout(new BoxLayout(p1,BoxLayout.X_AXIS));
			p1.add(new JLabel("                    "));
			p1.add(mainFrameViewp);
			midp.add(p1, "main");
			
			JPanel p2=new JPanel();
			p2.setLayout(new BoxLayout(p2,BoxLayout.X_AXIS));
			p2.add(new JLabel("                    "));
			p2.add(tabgColorp);
			midp.add(p2, "bgcolor");
			
			JPanel p3=new JPanel();
			p3.setLayout(new BoxLayout(p3,BoxLayout.X_AXIS));
			p3.add(new JLabel("                    "));
			p3.add(lineNumberp);
			midp.add(p3, "linenumber");
			viewlayout.show(midp,"main");
			
			south=new JPanel();
			bcon=new JButton("confirm");
			bcancel=new JButton("cancel");
			south.add(bcon);
			south.add(bcancel);
			
			viewset.add(north,"North");
			viewset.add(midp);
			viewset.add(south,"South");
			
			thisEvent();
			viewset.pack();
			viewset.setVisible(true);
		}
		private void thisEvent(){
			style.put("  Metal","javax.swing.plaf.metal.MetalLookAndFeel");
			style.put("  Nimbus", "javax.swing.plaf.nimbus.NimbusLookAndFeel");
			style.put("  Motif", "com.sun.java.swing.plaf.motif.MotifLookAndFeel");
			style.put("  Windows", "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			style.put("  Classic", "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
			colorSet.put("<html><p,style='color:white'>white</p></html>", 0xffffff);
			colorSet.put("<html><p,style='color:blue'>blue</p></html>", 0x8470FF);
			colorSet.put("<html><p,style='color:red'>red</p></html>", 0xff4500);
			colorSet.put("<html><p,style='color:gray'>gray</p></html>", 0x808080);
			colorSet.put("<html><p,style='color:green'>green</p></html>", 0x00FF7F);
			mainFrameView.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					viewlayout.show(midp, "main");
				}
			});
			tabgColor.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					viewlayout.show(midp, "bgcolor");
				}
			});
			lineNumber.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					viewlayout.show(midp, "linenumber");
	//				mainFrame.setVisible(false);
				}
			
			});
			bcon.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					Enumeration<AbstractButton> bm=styleSelect.getElements();
					while(bm.hasMoreElements()){
						AbstractButton temp=bm.nextElement();
						if(temp.isSelected()){
							try {
								UIManager.setLookAndFeel(style.get(temp.getText()));
							} catch (ClassNotFoundException e1) {
								e1.printStackTrace();
							} catch (InstantiationException e2) {
								e2.printStackTrace();
							} catch (IllegalAccessException e3) {
								e3.printStackTrace();
							} catch (UnsupportedLookAndFeelException e4) {
								e4.printStackTrace();
							}
							mainFrame.repaint();
						}
					}
					if(colorSelect.getSelection()==null){
						Color c=morecolor.getBackground();
						ta.setBackground(c);
					}
					else{
						Enumeration<AbstractButton> color=colorSelect.getElements();
						while(color.hasMoreElements()){
							AbstractButton stemp=color.nextElement();
							if(stemp.isSelected()){
								Color c=new Color(colorSet.get(stemp.getText()));
								ta.setBackground(c);
							}
						}
					}
//------------------------------------------------------------------------------------
					//有待进行行号的设置。。。
				}
			});
			morecolor.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					JColorChooser jcc=new JColorChooser();
					JDialog jdcolor=new JDialog(viewset,"color",true);
					jdcolor.add(jcc);
					jdcolor.setLocation(500, 200);
					jdcolor.pack();
					jdcolor.setVisible(true);
					morecolor.setBackground(jcc.getColor());
				}
			});
		}
	}
	private class editReplaceAction implements ActionListener{
		JDialog replaceDia;
		
		JTextField findl;
		JTextField replacel;
		
		JButton findb;
		JButton replaceb;
		JButton replaceAllb;
		JButton cancelb;
		
		String taText="";
		int templen=0;
		@Override
		public void actionPerformed(ActionEvent e) {
			replaceDia=new JDialog(mainFrame,"replace");
			replaceDia.setLayout(new GridLayout(1,2));
			replaceDia.setLocation(550, 250);
			
			findl=new JTextField(10);
			replacel=new JTextField(10);
			
			findb=new JButton("find");
			replaceb=new JButton("replace");
			replaceAllb=new JButton("replaceAll");
			cancelb=new JButton("cancel");
			
			JPanel pfind=new JPanel();
			pfind.add(new JLabel("find target: "));
			pfind.add(findl);
			
			JPanel preplace=new JPanel();
			preplace.add(new JLabel("replace by: "));
			preplace.add(replacel);
			
			JPanel left=new JPanel(new GridLayout(2,1));
			left.add(pfind);
			left.add(preplace);
			
			JPanel pbut=new JPanel(new GridLayout(2,2,5,5));
			pbut.add(findb);
			pbut.add(replaceb);
			pbut.add(replaceAllb);
			pbut.add(cancelb);
			
			replaceDia.add(left);
			replaceDia.add(pbut);
			
			thisEvent();
			taText=ta.getText();
			
			replaceDia.pack();
			replaceDia.setVisible(true);
		}
		private void thisEvent(){

			findb.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if("".equals(findl.getText())||"".equals(taText)){
						return;
					}
					int point =TextfindIndex(taText,findl.getText());
					if(point==-1){
						JDialog message=new JDialog(replaceDia,"tip!",true);
						message.setLocation(620,260);
						JPanel m=new JPanel();
						JLabel ml=new JLabel(" can not find! ");
						m.add(ml);
						message.add(m);
						message.pack();
						message.setVisible(true);
					}
					else{
						int TextLen=findl.getText().length();
						ta.select(point+templen, point+templen+TextLen);
						templen+=(point+TextLen);
						taText=taText.substring(point+TextLen);
					}
				}
			});
			replaceb.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					String tempr=replacel.getText();
					if("".equals(tempr)){
						return;
					}
					ta.replaceSelection(tempr);
				}
			});
			replaceAllb.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					String tempr=replacel.getText();
					String tempf=findl.getText();
					if(!"".equals(tempr)&&!"".equals(tempf)){
						ta.setText(taText.replaceAll(tempf, tempr));
					}
				}
			});
			cancelb.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					replaceDia.setVisible(false);
				}
			});
		}
		private int TextfindIndex(String basicText,String targetText){
			Pattern ptemp=Pattern.compile(targetText);
			Matcher mtemp=ptemp.matcher(basicText);
			if(mtemp.find()){
				return mtemp.start();
			}
			return -1;
		}
	}
}




