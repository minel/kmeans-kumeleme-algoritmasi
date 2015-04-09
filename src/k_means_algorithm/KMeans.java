package k_means_algorithm;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.math.plot.Plot2DPanel;
import org.math.plot.utils.Array;

public class KMeans {

	private static final int VERI_ADEDI = 30;
	private static final int KUME_ADEDI = 4;
	private static final int MAKS_ITERASYON = 10;

	public static void main(String[] args) {

		// Rastgele verileri üretmesi için veriUret metodunu çaðýr.
		double[][] kumelemeVerileri = veriUret(VERI_ADEDI);
		double[][] merkezler = merkezleriBelirle(kumelemeVerileri);

		// Bu dizi geçici olarak deðer saklayacak bir dizidir. Iterayon
		// sýrasýnda bir noktanýn diðer merkezlere olan uzaklýðýný saklar.
		double[] uzakliklar = new double[KUME_ADEDI];

		// Her bir verinin hangi gruba ait olduðunu saklar.
		int[] gruplar = new int[VERI_ADEDI];

		// Bir iterasyon sonunda oluþan gruplandýrmanýn tutulduðu dizidir.
		// Herhangi bir noktanýn grubunun deðiþip deðiþmediðini kontrol ederken
		// kullanýlýr.
		int[] geciciGruplar = new int[VERI_ADEDI];

		int[] gruptakiElemanSayilari = new int[KUME_ADEDI];

		// Üretilen kümeleme verilerini panele çizdir.
		Plot2DPanel panel = new Plot2DPanel();
		panel.addScatterPlot("Kmeans Algoritmasý", Color.RED, kumelemeVerileri);

		// Çerçevenin içerik panelini üstte oluþturulan (Plot2DPanel) panel
		// olarak ayarla ve göster.
		JFrame cerceve = new JFrame();
		cerceve.setContentPane(panel);
		cerceve.setSize(400, 400);
		cerceve.setVisible(true);

		int k;
		for (k = 0; k < MAKS_ITERASYON; k++) {
			// Noktalarý tek tek gez ve en yakýn gruba dahil et
			for (int i = 0; i < kumelemeVerileri.length; i++) {
				for (int j = 0; j < merkezler.length; j++) {
					// Klasik uzaklýk formülü
					uzakliklar[j] = Math
							.sqrt(Math.pow(kumelemeVerileri[i][0]
									- merkezler[j][0], 2)
									+ Math.pow(kumelemeVerileri[i][1]
											- merkezler[j][1], 2));
				}

				double[][] nokta = { { kumelemeVerileri[i][0],
						kumelemeVerileri[i][1] } };
				int minimumUzaklikIndeksi = minimumUzaklik(uzakliklar);
				gruplar[i] = minimumUzaklikIndeksi;
				Color grupRengi;

				switch (minimumUzaklikIndeksi) {
				case 0:
					grupRengi = Color.BLUE;
					break;
				case 1:
					grupRengi = Color.RED;
					break;
				case 2:
					grupRengi = Color.GREEN;
					break;
				case 3:
					grupRengi = Color.ORANGE;
					break;

				case 4:
					grupRengi = Color.MAGENTA;
					break;

				default:
					grupRengi = Color.MAGENTA;
					break;
				}

				panel.addScatterPlot("Kmeans Algoritmasý", grupRengi, nokta);
			}

			cerceve.repaint();

			if (k > 0 && !grupDegisikligiVarMi(gruplar, geciciGruplar)) {
				System.out.println("Algoritma sonladý. (" + k + " iterasyonda)");
				break;
			}

			// Yeni oluþan grup dizisini geciciGruplar'ýn üzerine yaz.
			System.arraycopy(gruplar, 0, geciciGruplar, 0, VERI_ADEDI);

			// Eski eleman sayýlarýný sýfýrla. (Her bir grup için hesaplanan
			// eleman sayýlarýný)
			for (int j = 0; j < gruptakiElemanSayilari.length; j++) {
				gruptakiElemanSayilari[j] = 0;
			}

			// Her bir gruba ait eleman sayýsýný hesapla
			for (int j = 0; j < VERI_ADEDI; j++) {
				gruptakiElemanSayilari[gruplar[j]]++;
			}

			// Merkezleri yeniden hesaplamak üzere sýfýrla
			for (int j = 0; j < KUME_ADEDI; j++) {
				merkezler[j][0] = 0;
				merkezler[j][1] = 0;
			}
			
			// Merkezleri yeniden hesaplama
			for (int i = 0; i < VERI_ADEDI; i++) {
				for (int j = 0; j < KUME_ADEDI; j++) {
					if(gruplar[i]== j){
						merkezler[j][0] += kumelemeVerileri[i][0];
						merkezler[j][1] += kumelemeVerileri[i][1];
					}
				}
			}
			
			// Ortalama alýp nihai yeni merkezleri belirleme
			for (int i = 0; i < merkezler.length; i++) {
				merkezler[i][0] = merkezler[i][0] / gruptakiElemanSayilari[i];
				merkezler[i][1] = merkezler[i][1] / gruptakiElemanSayilari[i];
			}
		}
	}

	/*
	 * Herbiri bir nokta olacak þekilde (yani x ve y koordinatý olan) rastgele
	 * veriler üretir.
	 * 
	 * @param veriSayisi Rastgele üretilecek veri sayýsýný belirtmek için
	 * kullanýlýr.
	 * 
	 * @return Üretilen noktalarý içeren 2 boyutlu double dizisi
	 */
	public static double[][] veriUret(int veriSayisi) {
		double veriler[][] = new double[veriSayisi][2];
		for (int i = 0; i < veriler.length; i++) {
			for (int j = 0; j < 2; j++) {
				veriler[i][j] = Math.random() * 10;
			}
		}
		return veriler;
	}
	
	
	/*
	 * 
	 * 
	 */
	public static double[][] merkezleriBelirle(double[][] kumelemeVerileri) {
		double merkezler[][] = new double[KUME_ADEDI][2];

		int secilenMerkez = 0;
		while (secilenMerkez < KUME_ADEDI) {
			int secili = (int) Math.floor(Math.random() * VERI_ADEDI);
			// Seçilen noktanýn daha önce merkezlere eklenip eklenmediðini
			// kontrol et.
			boolean seciliyiEkle = true;
			for (int i = 0; i < merkezler.length; i++) {
				if (ikiNoktaAyniMi(merkezler[i][0], merkezler[i][1],
						kumelemeVerileri[secili][0],
						kumelemeVerileri[secili][1])) {
					seciliyiEkle = false;
					break;
				}
			}

			if (seciliyiEkle) {
				// Nokta merkezler içinde deðilse merkezler içine ekle.
				merkezler[secilenMerkez][0] = kumelemeVerileri[secili][0];
				merkezler[secilenMerkez][1] = kumelemeVerileri[secili][1];
				secilenMerkez++;
			}
		}

		return merkezler;
	}

	/*
	 * Uzaklýklar dizisi içindeki minimum uzaklýðýn indeksini döndürür.
	 * 
	 * @param uzakliklar Uzaklýk deðerlerini içeren dizi
	 * 
	 * @return enKucukElemaninIndeksi En küçük uzaklýða sahip elemanýn indeksi
	 */
	public static int minimumUzaklik(double[] uzakliklar) {
		double enKucukEleman = uzakliklar[0];
		int enKucukElemaninIndeksi = 0;
		for (int i = 0; i < uzakliklar.length; i++) {
			if (enKucukEleman < uzakliklar[i]) {
				enKucukEleman = uzakliklar[i];
				enKucukElemaninIndeksi = i;
			}
		}
		return enKucukElemaninIndeksi;
	}

	/*
	 * Verilen iki noktanýn birbiriyle eþitliðini inceler
	 * 
	 * @param x1 Birinci noktanýn x koordinatý
	 * 
	 * @param y1 Birinci noktanýn y koordinatý
	 * 
	 * @param x2 Ýkinci noktanýn x koordinatý
	 * 
	 * @param y2 Ýkinci noktanýn y koordinatý
	 * 
	 * @return Ýki nokta birbirine eþitse <b>true</b>, deðilse <b>false</b>
	 * döner.
	 */
	public static boolean ikiNoktaAyniMi(double x1, double y1, double x2,
			double y2) {
		if (x1 == x2 && y1 == y2) {
			return true;
		}
		return false;
	}

	/*
	 * Iterasyonlardan sonra herhangi bir noktanýn grubunun deðiþip
	 * deðiþmediðini kontrol eder.
	 * 
	 * @param sonHal Verilere ait gruplarý içeren dizinin son hali
	 * 
	 * @param birOncekiHal Bir önceki iterasyonda belirlenen gruplarý içeren
	 * hal.
	 * 
	 * @return Herhangi bir verinin (noktanýn) grubu deðiþmiþse <b>true</b>,
	 * hiçbir noktanýn grubu deðiþmemiþse <b>false</b> döndürür.
	 */
	public static boolean grupDegisikligiVarMi(int[] sonHal, int[] birOncekiHal) {
		for (int i = 0; i < VERI_ADEDI; i++) {
			if (sonHal[i] != birOncekiHal[i]) {
				return true;
			}
		}
		return false;
	}
}
