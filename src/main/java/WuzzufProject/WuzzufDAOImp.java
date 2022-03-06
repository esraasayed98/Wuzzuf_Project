package WuzzufProject;


//import joinery.DataFrame;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import smile.data.DataFrame;
import org.apache.commons.csv.CSVFormat;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

import smile.data.Tuple;
import smile.data.vector.IntVector;
import smile.io.Read;
import tech.tablesaw.api.Table;


public class WuzzufDAOImp {
    private List<WuzzufPojo> Data_List = new ArrayList<WuzzufPojo>();
    private  DataFrame Data = null;

    private List<String> skills=new ArrayList<>() ;
    private Map<Long,Object> chartMap;


    public WuzzufDAOImp(String path) throws IOException, URISyntaxException {

        CSVFormat format = CSVFormat.DEFAULT.withFirstRecordAsHeader ();
        try
        {
            Data= Read.csv (path, format).select("Title","Company","Location","Type","Level","YearsExp","Country","Skills");


            Data.stream().map(r -> r.getString("Skills")).forEach(x ->
                    Collections.addAll(skills,x.split(",")));
        }

        catch (IOException  | URISyntaxException e)
        {
            e.printStackTrace();
        }
    }

    public String Read_data()
    {
        return Data.slice(0,10).toString();
    }
    public String Summary()
    {


       return Data.summary().toString();
    }

    public String WuzzufDataStructure()
    {
        return Data.structure().toString();
    }
    public String clean()
    {
        return "Number of rows in data= "+ String.valueOf( Data.omitNullRows().nrows()) +
                ", Number of rows after removing null values= " +String.valueOf( Data.nrows()) ;
    }

    public long RemoveDuplicates()
    {




        try {
            Table t = Table.read().file("src/main/resources/Wuzzuf_Jobs.csv");
            t= t.dropDuplicateRows() ;
            return t.stream().count() ;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;


    }

    public Map<Long,Object> getCount(String col)
    {

        Map<Object,Long> CompanyJobs= Data.stream().collect(Collectors.groupingBy(t -> t.getString(col), Collectors.counting()));
        TreeMap<Long,Object> DATA = new TreeMap<>();

        for ( Map.Entry<Object,Long> i: CompanyJobs.entrySet())
        {
            if (i.getValue() > 10)
            {

                DATA.put(i.getValue(), i.getKey() );

            }

        }


        return DATA.descendingMap();


    }

    public PieChart DrawPie(Map<Long,Object> data ) throws IOException {

        PieChart chart = new PieChartBuilder().width (800).height (600).title ("Jobs Pie Chart").build ();
       List<String> value=data.values().stream().map(t->t.toString()).collect(Collectors.toList());
       List<Integer> count= data.keySet().stream().map(t-> t.intValue()).collect(Collectors.toList());

       for (int i=0 ;i<value.size();i++)
       {
           chart.addSeries(value.get(i),count.get(i));

       }

        return chart;

    }

    public CategoryChart DrawBar(Map<Long,Object> data , String col1, String col2) throws IOException {

        CategoryChart chart = new CategoryChartBuilder().width (1024).height (768).title ("Most Popular " + col1 +"s" ).xAxisTitle ("Job Titles").yAxisTitle ("Count").build ();
        List<String> value=data.values().stream().map(t->t.toString()).collect(Collectors.toList());
        List<Integer> count= data.keySet().stream().map(t-> t.intValue()).collect(Collectors.toList());

        chart.getStyler ().setLegendPosition (Styler.LegendPosition.InsideNW);
        chart.getStyler ().setHasAnnotations (true);
        chart.getStyler().setXAxisLabelRotation(90);
        chart.addSeries ( col1 + col2 , value, count);

        return chart ;

    }

    public Map<Integer,String>   count_skills ()

    {

      Map<String ,Integer > m = new TreeMap<>();


      for (String s : skills) {
        if (!(s.equals(null))) {
            if (m.containsKey(s)) {
                m.put(s, m.get(s) + 1);
            } else {
                m.putIfAbsent(s, 1);
            }
        }
      }


      TreeMap<Integer,String> TopSkills = new TreeMap<>();

        for ( Map.Entry<String,Integer> i: m.entrySet())
        {
            if (i.getValue() > 10)
            {

                TopSkills.put(i.getValue(), i.getKey() );

            }

        }


      return TopSkills.descendingMap();
    }
    public String Factorize ()
    {


        List<String> years= Data.select("YearsExp").stream().map(x -> x.getString(0).replace("Yrs of Exp","" )).collect(Collectors.toList());


        List<String> years_distinct= Data.select("YearsExp").stream().map(x -> x.getString(0).replace("Yrs of Exp","" )).distinct().collect(Collectors.toList());


        List<Integer> categories=new ArrayList<>();
        for(String y : years)
        {
            categories.add(years_distinct.indexOf(y));

        }
        Data=Data.merge(IntVector.of( "Years Exp encoded",categories.stream().mapToInt(i->i).toArray()));



        return Data.slice(0,10).toString();
    }




}

