import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
// first commit
class NaiveBayes{

	public static void main(String[] args) throws IOException {
		ArrayList<String> lines = new ArrayList<String>();
		FileInputStream fis = new FileInputStream("train_full_B.tsv");
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String line = null;
		while((line = br.readLine())!=null)
			lines.add(line);
		br.close();
		HashMap<String,Integer> pos = new HashMap<String,Integer>();
		HashMap<String,Integer> neg = new HashMap<String,Integer>();
		HashMap<String,Integer> obj = new HashMap<String,Integer>();
		HashSet<String> vocabulary = new HashSet<String>();
		int countPos = 0;
		int countNeg = 0;
		int countObj = 0;
		for(int i=0;i<lines.size();i++){
			String list[] = lines.get(i).split("\\s+");
			switch(list[2]){
				case "positive":
				{
					countPos++;
					for(int j=3;j<list.length;j++){
						vocabulary.add(list[j]);
						if(pos.containsKey(list[j])){
							pos.put(list[j],pos.get(list[j])+1);
						}
						else{
							pos.put(list[j],1);
						}
					}
				}
				break;
				case "negative":
				{
					countNeg++;
					for(int j=3;j<list.length;j++){
						vocabulary.add(list[j]);
						if(neg.containsKey(list[j])){
							neg.put(list[j],neg.get(list[j])+1);
						}
						else{
							neg.put(list[j],1);
						}
					}
				}
				break;
				default :
				{
					countObj++;
					for(int j=3;j<list.length;j++){
						vocabulary.add(list[j]);
						if(obj.containsKey(list[j])){
							obj.put(list[j],obj.get(list[j])+1);
						}
						else{
							obj.put(list[j],1);
						}
					}
						
				}
				break;
			}
		}

		double posProb = Math.log10(countPos)-Math.log10(countPos+countNeg+countObj);
		double negProb = Math.log10(countNeg)-Math.log10(countPos+countNeg+countObj);
		double objProb = Math.log10(countObj)-Math.log10(countPos+countNeg+countObj);

		ArrayList<String> res = new ArrayList<String>();
		ArrayList<String> result = new ArrayList<String>();

		FileInputStream f = new FileInputStream("Test-set.txt");
		BufferedReader b = new BufferedReader(new InputStreamReader(f));
		String str = null;
		while((str = b.readLine())!=null)
			res.add(str);
		b.close();

		//No of words in positive set
		int posWord = 0;
		for(int f1:pos.values())
			posWord+=f1;

		//No of words in positive set
		int negWord = 0;
		for(int f2:neg.values())
			negWord+=f2;

		//No of words in positive set
		int objWord = 0;
		for(int f3:obj.values())
			objWord+=f3;

		double p = posProb;
		double n = negProb;
		double o = objProb;

		for(int i=0;i<res.size();i++){
			String list[] = res.get(i).split("\\s+");
			p = posProb;
			n = negProb;
			o = objProb;
			int vSize = vocabulary.size();
			for(int j=3;j<list.length;j++){
				//for positive set
				if(pos.get(list[j])!=null){
					p += (Math.log10(pos.get(list[j])+1) - Math.log10(posWord+vSize));
				}
				//for negative set
				if(neg.get(list[j])!=null){
					n += (Math.log10(neg.get(list[j])+1) - Math.log10(negWord+vSize));
				}
				//for neutral set
				if(obj.get(list[j])!=null){
					o += (Math.log10(obj.get(list[j])+1) - Math.log10(objWord+vSize));
				}
			}
			if(p>o&&p>n){
				list[2] = "positive";
			}	
			else if(n>p&&n>o){
				list[2] = "negative";
			}
			else if(o>p&&o>n){
				list[2] = "neutral";
			}
			String temp = "";
			for(int j=0;j<3;j++){
				temp = temp+list[j]+"\t";
			}
			for(int j=3;j<list.length;j++){
				temp = temp+list[j]+" ";
			}
			result.add(temp);		
		}
		
		PrintWriter out = null;
		try{
			out = new PrintWriter(new FileWriter("output.txt"));
			for(String text:result){
				out.println(text);
			}
		}
		catch(IOException e){
			System.err.println(e.getMessage());
		}
		finally{
			if(out!=null){
				out.close();
			}
		}
	}
}
