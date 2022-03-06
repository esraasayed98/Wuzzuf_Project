package WuzzufProject;
import org.knowm.xchart.BitmapEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

@RestController
public class JobsController {
    String path="src/main/resources/Wuzzuf_Jobs.csv" ;

    WuzzufDAOImp DAO=new WuzzufDAOImp(path);

    public JobsController() throws IOException, URISyntaxException {
    }

    @GetMapping("/read")
    public String read()
    {
       return getHtmlTable( DAO.Read_data(),"First 10 rows in the data");
    }

    @GetMapping("/summary")
    public String describe()
    {

        return   getHtmlTable(  DAO.Summary(),"Summary of wuzzuf data ") ;
    }
    @GetMapping("/schema")
    public String Schema()
    {
        return getHtmlTable(DAO.WuzzufDataStructure(),"Structure of wuzzuf data");
    }
    @GetMapping("/clean")
    public String clean()
    {
        return DAO.clean();
    }

    @GetMapping("/Duplicates")
    public String Duplicates()
    {
        return DAO.RemoveDuplicates();
    }

    @GetMapping("/count")
    public String jobsforcompany(@RequestParam(value="col") String name)
    {
        return getTableFromMap(DAO.getCount(name),name,name+" count");
    }

    @GetMapping("/pie")
    public StringBuilder viewPie() throws IOException {
        Map<Long, Object> data = DAO.getCount("Company");
        String path = "/images/Sample_Pie_Chart.png";
        try {
            BitmapEncoder.saveBitmap(DAO.DrawPie(data), "src/main/resources/static" + path, BitmapEncoder.BitmapFormat.PNG);
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("<h1>Companies Pie Chart</h1>");
        builder.append("<img src='").append(path).append("'/>");

        return builder;
    }

    @GetMapping("/barTitle")
    public StringBuilder viewBar() throws IOException {
        Map<Long,Object> data = DAO.getCount("Title");
        String path="/images/Sample_Bar1_Chart.png" ;
        try {
            BitmapEncoder.saveBitmap(DAO.DrawBar(data,"Titles","Count"), "src/main/resources/static"+ path, BitmapEncoder.BitmapFormat.PNG);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("<h1>Titles Bar Chart</h1>");
        builder.append("<img src='").append(path).append("'/>");

        return builder;


    }
    @GetMapping("/barArea")
    public StringBuilder viewBar2() throws IOException {
        Map<Long,Object> data = DAO.getCount("Location");
        String path="/images/Sample_Bar2_Chart.png" ;
        try {
            BitmapEncoder.saveBitmap(DAO.DrawBar(data,"Area","Count"), "src/main/resources/static"+ path, BitmapEncoder.BitmapFormat.PNG);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("<h1>Areas Bar Chart</h1>");
        builder.append("<img src='").append(path).append("'/>");

        return builder;
    }
    @GetMapping("/skill")
    public String getSkills()
    {
        return  getTableFromMap(DAO.count_skills(),"skills","skills count");
    }

    @GetMapping("/factorize")
    public String factorize()
    {

        return  getHtmlTable(DAO.Factorize(),"Data after factorizing YearsExp column");
    }
    private String getHtmlTable(String datatext,String tableTitle)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<style>\n" +
                "table {\n" +
                "  font-family: arial, sans-serif;\n" +
                "  border-collapse: collapse;\n" +
                "  width: 100%;\n" +
                "}\n" +
                "\n" +
                "td, th {\n" +
                "  border: 1px solid #dddddd;\n" +
                "  text-align: left;\n" +
                "  padding: 8px;\n" +
                "}\n" +
                "\n" +
                "tr:nth-child(even) {\n" +
                "  background-color: #dddddd;\n" +
                "}\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n");

        builder.append("<h1>"+tableTitle+"</h1>");
        builder.append("<table>");
        String[] dataArray = datatext.split("\n");
        for (String x : dataArray )
        {
            if(!(x.contains("--")||x.contains("[")))
            {
                builder.append("<tr><td>" + x.replace("|", "</td><td>") + "</td></tr>");
            }
        }
        builder.append("</table>\n" +
                "\n" +
                "</body>\n" +
                "</html>");
        return builder.toString();
    }

    private String getTableFromMap(Map <? extends Object,? extends Object> m,String col_name, String tableTitle)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<style>\n" +
                "table {\n" +
                "  font-family: arial, sans-serif;\n" +
                "  border-collapse: collapse;\n" +
                "  width: 100%;\n" +
                "}\n" +
                "\n" +
                "td, th {\n" +
                "  border: 1px solid #dddddd;\n" +
                "  text-align: left;\n" +
                "  padding: 8px;\n" +
                "}\n" +
                "\n" +
                "tr:nth-child(even) {\n" +
                "  background-color: #dddddd;\n" +
                "}\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n");
        builder.append("<h1>"+tableTitle+"</h1>");
        builder.append("<table>");
        builder.append("<tr><th>" + col_name+ "</th><th>"+ "count"+"</th></tr>");
        for (Map.Entry x : m.entrySet() )
        {

            builder.append("<tr><td>" +x.getValue()+ "</td><td>"+   x.getKey()+"</td></tr>");

        }
        builder.append("</table>\n" +
                "\n" +
                "</body>\n" +
                "</html>");
        return builder.toString();
    }


}
