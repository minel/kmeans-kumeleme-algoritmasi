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

		// Rastgele verileri �retmesi i�in veriUret metodunu �a��r.
		double[][] kumelemeVerileri = veriUret(VERI_ADEDI);
		double[][] merkezler = merkezleriBelirle(kumelemeVerileri);

		// Bu dizi ge�ici olarak de�er saklayacak bir dizidir. Iterayon
		// s�ras�nda bir noktan�n di�er merkezlere olan uzakl���n� saklar.
		double[] uzakliklar = new double[KUME_ADEDI];

		// Her bir verinin hangi gruba ait oldu�unu saklar.
		int[] gruplar = new int[VERI_ADEDI];

		// Bir iterasyon sonunda olu�an grupland�rman�n tutuldu�u dizidir.
		// Herhangi bir noktan�n grubunun de�i�ip de�i�medi�ini kontrol ederken
		// kullan�l�r.
		int[] geciciGruplar = new int[VERI_ADEDI];

		int[] gruptakiElemanSayilari = new int[KUME_ADEDI];

		// �retilen k�meleme verilerini panele �izdir.
		Plot2DPanel panel = new Plot2DPanel();
		panel.addScatterPlot("Kmeans Algoritmas�", Color.RED, kumelemeVerileri);

		// �er�evenin i�erik panelini �stte olu�turulan (Plot2DPanel) panel
		// olarak ayarla ve g�ster.
		JFrame cerceve = new JFrame();
		cerceve.setContentPane(panel);
		cerceve.setSize(400, 400);
		cerceve.setVisible(true);

		int k;
		for (k = 0; k < MAKS_ITERASYON; k++) {
			// Noktalar� tek tek gez ve en yak�n gruba dahil et
			for (int i = 0; i < kumelemeVerileri.length; i++) {
				for (int j = 0; j < merkezler.length; j++) {
					// Klasik uzakl�k form�l�
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

				panel.addScatterPlot("Kmeans Algoritmas�", grupRengi, nokta);
			}

			cerceve.repaint();

			if (k > 0 && !grupDegisikligiVarMi(gruplar, geciciGruplar)) {
				System.out.println("Algoritma sonlad�. (" + k + " iterasyonda)");
				break;
			}

			// Yeni olu�an grup dizisini geciciGruplar'�n �zerine yaz.
			System.arraycopy(gruplar, 0, geciciGruplar, 0, VERI_ADEDI);

			// Eski eleman say�lar�n� s�f�rla. (Her bir grup i�in hesaplanan
			// eleman say�lar�n�)
			for (int j = 0; j < gruptakiElemanSayilari.length; j++) {
				gruptakiElemanSayilari[j] = 0;
			}

			// Her bir gruba ait eleman say�s�n� hesapla
			for (int j = 0; j < VERI_ADEDI; j++) {
				gruptakiElemanSayilari[gruplar[j]]++;
			}

			// Merkezleri yeniden hesaplamak �zere s�f�rla
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
			
			// Ortalama al�p nihai yeni merkezleri belirleme
			for (int i = 0; i < merkezler.length; i++) {
				merkezler[i][0] = merkezler[i][0] / gruptakiElemanSayilari[i];
				merkezler[i][1] = merkezler[i][1] / gruptakiElemanSayilari[i];
			}
		}
	}

	/*
	 * Herbiri bir nokta olacak �ekilde (yani x ve y koordinat� olan) rastgele
	 * veriler �retir.
	 * 
	 * @param veriSayisi Rastgele �retilecek veri say�s�n� belirtmek i�in
	 * kullan�l�r.
	 * 
	 * @return �retilen noktalar� i�eren 2 boyutlu double dizisi
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
			// Se�ilen noktan�n daha �nce merkezlere eklenip eklenmedi�ini
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
				// Nokta merkezler i�inde de�ilse merkezler i�ine ekle.
				merkezler[secilenMerkez][0] = kumelemeVerileri[secili][0];
				merkezler[secilenMerkez][1] = kumelemeVerileri[secili][1];
				secilenMerkez++;
			}
		}

		return merkezler;
	}

	/*
	 * Uzakl�klar dizisi i�indeki minimum uzakl���n indeksini d�nd�r�r.
	 * 
	 * @param uzakliklar Uzakl�k de�erlerini i�eren dizi
	 * 
	 * @return enKucukElemaninIndeksi En k���k uzakl��a sahip eleman�n indeksi
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
	 * Verilen iki noktan�n birbiriyle e�itli�ini inceler
	 * 
	 * @param x1 Birinci noktan�n x koordinat�
	 * 
	 * @param y1 Birinci noktan�n y koordinat�
	 * 
	 * @param x2 �kinci noktan�n x koordinat�
	 * 
	 * @param y2 �kinci noktan�n y koordinat�
	 * 
	 * @return �ki nokta birbirine e�itse <b>true</b>, de�ilse <b>false</b>
	 * d�ner.
	 */
	public static boolean ikiNoktaAyniMi(double x1, double y1, double x2,
			double y2) {
		if (x1 == x2 && y1 == y2) {
			return true;
		}
		return false;
	}

	/*
	 * Iterasyonlardan sonra herhangi bir noktan�n grubunun de�i�ip
	 * de�i�medi�ini kontrol eder.
	 * 
	 * @param sonHal Verilere ait gruplar� i�eren dizinin son hali
	 * 
	 * @param birOncekiHal Bir �nceki iterasyonda belirlenen gruplar� i�eren
	 * hal.
	 * 
	 * @return Herhangi bir verinin (noktan�n) grubu de�i�mi�se <b>true</b>,
	 * hi�bir noktan�n grubu de�i�memi�se <b>false</b> d�nd�r�r.
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
