package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.util.TimerTask;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;


public class Controller {

    public static int indexForWhileShow;
    public static int in = 0;

    public ImageView imageV = new ImageView();
    public Button showImgB;
    public ListView<Double> slices;
    public TextField name;
    public TextField slLoc;
    public TextField serNum;
    public TextField instNum;
    public TextField acquNum;
    public ToggleButton startB;

    public Timer timer = new Timer();
    public List<DICOMImage> listImage = new ArrayList<>();
    public String pathName = "http://195.3.158.22:8888/lab6/CT/dataset02";
    public List<Double> listSlices = new ArrayList<>();
    public ArrayList<ArrayList<DICOMImage> > aList = new ArrayList<>();
    private boolean flag;

    public void readFromURL()
    {
        try {
            URL url = new URL(pathName);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String inputLine;
            while ((inputLine = br.readLine()) != null)
            {
                try
                {
                    DICOMImage dicomImage = new DICOMImage(pathName, inputLine);
                    name.setText(dicomImage.FileName);
                    slLoc.setText(String.valueOf(dicomImage.SliceLocation));
                    serNum.setText(String.valueOf(dicomImage.SeriesNumber));
                    instNum.setText(String.valueOf(dicomImage.InstNumber));
                    acquNum.setText(String.valueOf(dicomImage.AcquNumber));
                    listImage.add(dicomImage);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            sort();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showIMG(int index)
    {
        imageV.setImage(SwingFXUtils.toFXImage(listImage.get(index).getImg(), null));
        name.setText(listImage.get(index).FileName);
        slLoc.setText(String.valueOf(listImage.get(index).SliceLocation));
        serNum.setText(String.valueOf(listImage.get(index).SeriesNumber));
        instNum.setText(String.valueOf(listImage.get(index).InstNumber));
        acquNum.setText(String.valueOf(listImage.get(index).AcquNumber));
    }


    public void loadImg(ActionEvent actionEvent) throws MalformedURLException {
       showImgB.setDisable(true);
       MyThread thread = new MyThread();
       thread.start();
    }


    public void sort()
    {
        Collections.sort(listImage);
    }

    public void countGroupSlice()
    {
        ObservableList<Double> list = FXCollections.observableArrayList();

        listSlices.add(listImage.get(0).SliceLocation);
        for(int i = 1; i < listImage.size(); i ++)
        {
            if(listImage.get(i).SliceLocation == listImage.get(i-1).SliceLocation){}
            else
            {
                listSlices.add(listImage.get(i).SliceLocation);
            }

        }
        list.addAll(listSlices);


        for (Double listSlice : listSlices) {
            ArrayList<DICOMImage> a = new ArrayList<>();
            for (DICOMImage dicomImage : listImage) {
                if (dicomImage.SliceLocation == listSlice) {
                    a.add(dicomImage);
                }
            }
            aList.add(a);
        }
        slices.setItems(list);
        System.out.println((aList));
        System.out.println(listSlices);
    }

    public void loopShow()
    {
        ArrayList<Integer> arrIndices = new ArrayList<>();
        for (int i = 0; i < listImage.size(); i++) {
            for (int j = 0; j < aList.get(slices.getSelectionModel().getSelectedIndex()).size(); j++) {
                if (listImage.get(i).InstNumber == aList.get(slices.getSelectionModel().getSelectedIndex()).get(j).InstNumber) {
                    arrIndices.add(i);
                }
            }
        }
        if(in == arrIndices.size() - 1)
        {
            in = 0;
        }
        else{
            indexForWhileShow = arrIndices.get(in++);
        }
        showIMG(indexForWhileShow);
    }

    public void play(ActionEvent actionEvent) {
        flag = true;
    }

    public void pause(ActionEvent actionEvent) {
        flag = false;
    }

    public void start(ActionEvent actionEvent) {
        timer.schedule(new MyTask(), 0, 2000);
        startB.setDisable(true);
    }

    public void changeSlice(MouseEvent mouseEvent) {}

    public class MyTask extends TimerTask {

        @Override
        public void run() {
            if(flag)
            {
                loopShow();
            }
            else
            {
                System.out.println("pause");
            }
        }

    }

    public class MyThread extends Thread
    {
        public void run()
        {
            System.out.println("began");
            try
            {
                readFromURL();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            countGroupSlice();
            showIMG(0);
            slices.getSelectionModel().select(0);
        }
    }
}
