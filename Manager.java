import java.io.*;
import java.util.ArrayList;

public class Manager{

    //int[] physical_memory = new int[524288];


    public static void main(String[] args)throws Exception{
        //initilization of variable
        int[] physical_memory = new int[524288];
        String file_line;
        //Reading in the init file for physical memory
        File file = new File("my_init_input.txt");

        BufferedReader br = new BufferedReader(new FileReader(file));

        //Taking the init in Line 1 and Line 2
        ArrayList<Integer> init_segment = new ArrayList<Integer>();
        ArrayList<Integer> init_page = new ArrayList<Integer>();

        int line_one = 1;
        while( (file_line = br.readLine()) != null){
            String split_line[] = file_line.split(" ");
            if(line_one == 1){
                for(int i = 0; i < split_line.length; i++){
                        init_segment.add(Integer.parseInt(split_line[i]));
                }
                line_one = 0;
            }
            else if(line_one == 0){
                for(int i = 0; i < split_line.length; i++){
                    init_page.add(Integer.parseInt(split_line[i]));
                }
            }
                
        }
        /*
        for(int i = 0; i < init_segment.size(); i++){
            System.out.print(init_segment.get(i) + " ");
        }
        System.out.println();
        for(int i = 0; i < init_page.size(); i++){
            System.out.print(init_page.get(i) + " ");
        }
        */

        //Initialization of Physical Memory from init
        //Initialization the segment table
        int i = 0;
        while(i < init_segment.size()){
            physical_memory[2 * init_segment.get(i)] = init_segment.get(i+1);
            physical_memory[2 * init_segment.get(i) + 1] = init_segment.get(i+2);
            i += 3;
        }
        /*
        System.out.println("first segment: " + physical_memory[12] + " " + physical_memory[13]);
        System.out.println("second segment: " + physical_memory[2] + " " + physical_memory[3]);
        System.out.println("third segment: " + physical_memory[6] + " " + physical_memory[7]);
        */

        //Initialization of the page table
        int j = 0;
        while(j < init_page.size()){
            if(physical_memory[2 * init_page.get(j) + 1] == 0){
                j += 3;
            }
            else{
                physical_memory[physical_memory[2 * init_page.get(j) + 1] * 512 + init_page.get(j+1)] = init_page.get(j + 2);
                j += 3;
            }
        }
        /*
        System.out.println("first segment: " + physical_memory[1024]);
        System.out.println("second segment: " + physical_memory[1025]);
        System.out.println("third segment: " + physical_memory[2560]);
        System.out.println("fourth segment: " + physical_memory[3071]);
        System.out.println("7 segment: " + physical_memory[1536]);
        System.out.println("7 segment: " + physical_memory[1538]);
        */

        //Executing Virtual Address Translations
        
        //Accepting VA 
        File input_file = new File("my_input.txt");

        BufferedReader input_br = new BufferedReader(new FileReader(input_file));
        
        //Reading the input
        String input_line;
        ArrayList<Integer> virtual_addresses = new ArrayList<Integer>();

        while((input_line = input_br.readLine()) != null){
            String split_input[] = input_line.split(" ");
            for(int l = 0; l < split_input.length; l++){
                virtual_addresses.add(Integer.parseInt(split_input[l]));
            }
        }

        //check input
        /*
        for(int k = 0; k < virtual_addresses.size(); k++){
            System.out.print(virtual_addresses.get(k) + " ");
        }
        */
        ArrayList<VA> va_object_list = new ArrayList<VA>();
        for(int k = 0; k < virtual_addresses.size(); k++){
            int va = virtual_addresses.get(k);
            int s, w, p, pw;
            s = w = pw = 0;
            int mask = 0x1FF;
            int mask2 = 0x3FFF;


            s = va >> 18;
            w = va & mask;
            p = (va >> 9) & mask;
            pw = va & mask2;
            VA address = new VA(s, w, p, pw);
            va_object_list.add(address);
        }
        /*
        for(int k = 0; k < va_object_list.size(); k++){
            System.out.println(va_object_list.get(k).s + " " + va_object_list.get(k).w + " " + va_object_list.get(k).p + " " + va_object_list.get(k).pw);
        }
        */
        //Translating the virtual addresses
        int pa = 0;
        for(VA obj: va_object_list){
            //System.out.print(physical_memory[2 * obj.s]);
            if(obj.pw >= physical_memory[2 * obj.s]){
                System.out.print(-1 + " ");
            }
            else{
                pa  = physical_memory[physical_memory[2 * obj.s + 1] * 512 + obj.p] * 512 + obj.w;
                System.out.print(pa + " ");
            }
        }
        

        br.close();
        input_br.close();

    }
}