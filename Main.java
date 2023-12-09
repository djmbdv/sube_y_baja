import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
/**
 * InnerMain
 */
class Carta {
    public int  number;
    public int shape;
    public Image image;

    public static int ANCHO_CARTA = 201;
    public static int ALTO_CARTA = 320;
    public static File[] files = new File[4];

    public Carta(int number, int shape) throws IOException{
        this.number = number > 7 ? number + 2 : number;
        this.shape = shape;

        if(Carta.files[shape]== null){
            Carta.files[shape] = new File(getShapeString().toLowerCase() + ".jpg");

        }
        this.image = ImageIO.read(Carta.files[shape]).getSubimage(ANCHO_CARTA * ((number-1)%5), ALTO_CARTA * ((number-1)/5),
        ANCHO_CARTA , ALTO_CARTA);
    }

    public String getShapeString(){
    String shapeString ="";
       switch (this.shape) {
            case 0:
                shapeString = "Oro";
                break;
            case 1:
                shapeString = "Copas";
                break;
            case 2:
                shapeString = "Espada";
                break;
            case 3:
                shapeString = "Basto";
                break;
        
            default:
                break;
        }

        return shapeString;

    }
    @Override
    public String toString(){
        String shapeString = "";
        switch (this.shape) {
            case 0:
                shapeString = "Oro";
                break;
            case 1:
                shapeString = "Copas";
                break;
            case 2:
                shapeString = "Espada";
                break;
            case 3:
                shapeString = "Basto";
                break;
        
            default:
                break;
        }
        return "Carta{" + "number=" + number + " ," + shapeString + "}";
    }
}


class Main extends JFrame {
    private JButton bBaja;
    private JButton bSube;
    private JButton bIgual;
    private JPanel panel;
    private JLabel labelLastCarta;
    public List<Carta> mazo;
    public Carta lastCarta;
    public JLabel cardCount;
    public JPanel panelCarta;


    public Carta getCarta(){
        System.out.println("getCarta");
        
        int index =  ((int)(Math.random() * mazo.size()))%mazo.size();
        Carta selectedCard = mazo.get(index);
        mazo.remove(index);
        System.out.println(mazo.size());
        return selectedCard; 
    }

    public void showLost(){
        
        JOptionPane.showMessageDialog(this.panel,"1 shut", "Bebe", JOptionPane.WARNING_MESSAGE);
    }

    public void showDoubleLost(){
        JOptionPane.showMessageDialog(this.panel,"2 shut", "Bebe Doble", JOptionPane.WARNING_MESSAGE);
    }

    public void showDoubleWin(){
        JOptionPane.showMessageDialog(this.panel,"2 shut", "Eliges Quiien bebe", JOptionPane.WARNING_MESSAGE);
    }

    public void gameOver(){
        JOptionPane.showMessageDialog(this.panel,"FONDO BLANCO", "Game Over", JOptionPane.OK_OPTION);
    }
    public void gol(String action){
        
        Carta newCarta = getCarta();
        this.labelLastCarta.setForeground(newCarta.number >  this.lastCarta.number ? Color.GREEN : Color.RED);
        this.labelLastCarta.setForeground(newCarta.number ==  this.lastCarta.number ? Color.BLACK : Color.RED);
        this.labelLastCarta.setText(newCarta.toString());
        this.cardCount.setText("Restan " + String.valueOf(mazo.size()) + " cartas.");   
        switch (action) {
            case "up":
                if(newCarta.number  <  lastCarta.number) showLost();
                else if(newCarta.number  ==  lastCarta.number) showDoubleLost();
                break;
            case "down":
                if(newCarta.number  >  lastCarta.number) showLost();
                else if(newCarta.number  ==  lastCarta.number) showDoubleLost();
                break;
            case "same":
                if(newCarta.number  != lastCarta.number) showLost();
                else if(newCarta.number  ==  lastCarta.number) showDoubleWin();
            default:
                break;
        }
        if(mazo.size() == 0) gameOver();
        this.cardCount.repaint();
        this.lastCarta = newCarta;
        this.labelLastCarta.repaint();
       this.panel.repaint();;
    }
    public void initMazo() throws IOException{
        this.mazo = new LinkedList<Carta>();
        for(int i  = 0; i < 40; i++){
            mazo.add(new Carta(i%10 + 1, i/10 ));
        }
    }

    public Main() throws IOException{
        super("Carticas");
        initMazo();
        
        BufferedImage i = ImageIO.read(new File("oro.jpg"));  
        
        this.panelCarta = new JPanel(){
            @Override
            public void paint(Graphics g){
                super.paint(g);
                Graphics2D g2 = (Graphics2D)g;
                g2.drawImage(lastCarta.image, 0, 0, this);

            }
        };

        this.panelCarta.setSize(125, 300);
        this.panelCarta.setMinimumSize(new Dimension(125,300));
        this.panelCarta.setBackground(Color.gray);
      //  this.panelCarta
        this.panelCarta.setLayout(new FlowLayout());
        this.lastCarta = getCarta();
        this.cardCount = new JLabel("Restan " + String.valueOf(mazo.size()) + " cartas.");   
        this.panel = new JPanel();
        this.panel.setLayout(new GridLayout(3,4));
        this.labelLastCarta = new JLabel(lastCarta.toString());
        for (Carta c : mazo) {
            System.out.println(c);
        }
        this.setSize(800,700);
        
        this.setDefaultCloseOperation(3);
        setVisible(true);
        
        this.bBaja = new JButton("Baja ");
        this.bSube = new JButton("Sube ^");
        this.bIgual = new JButton("Igual =");
        bBaja.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent ae) {
                gol("down");
           }
        });
        bSube.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent ae) {
                gol("up");
           }
        });
        bIgual.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent ae) {
                gol("same");
           }
        });
        this.panel.add(panelCarta);
        this.panel.add(this.labelLastCarta);
        this.panel.add(bBaja);
        this.panel.add(bSube);
        this.panel.add(bIgual);
        this.panel.add(cardCount);
        
        this.add(panel);
        repaint();
    }

    public static void main(String[] args) throws IOException {
        new Main();
    }
}