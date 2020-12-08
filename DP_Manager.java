import java.io.*;
import java.util.ArrayList;

public class DP_Manager{
    //int[] physical_memory = new int[524288];


    public static void main(String[] args)throws Exception{
        //initilization of variable
        int[] physical_memory = new int[524288];
        int[][] paging_disk = new int[1024][512];
        int[] free_frame_list = new int[1024];

        for(int k = 2; k < free_frame_list.length; k++){
            free_frame_list[k] = 1;
        }

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
        int i = 0;
        while(i < init_segment.size()){
            physical_memory[2 * init_segment.get(i)] = init_segment.get(i+1);
            physical_memory[2 * init_segment.get(i) + 1] = init_segment.get(i+2);
            if(init_segment.get(i+2) > 0){
                free_frame_list[init_segment.get(i+2)] = 0;
            }
            i += 3;
        }
        //Initialization of the page table
        int j = 0;
        while(j < init_page.size()){
            if(physical_memory[2 * init_page.get(j) + 1] == 0){
                j += 3;
            }
            else if(physical_memory[2 * init_page.get(j) + 1] < 0){
                paging_disk[-1 * physical_memory[2 * init_page.get(j) + 1]][init_page.get(j + 1)] = init_page.get(j+ 2);
                if(init_page.get(j+2) > 0){
                    free_frame_list[init_page.get(j+2)] = 0;
                }
                j += 3;
            }
            else{
                physical_memory[physical_memory[2 * init_page.get(j) + 1] * 512 + init_page.get(j+1)] = init_page.get(j + 2); 
                if(init_page.get(j+2) > 0){
                    free_frame_list[init_page.get(j+2)] = 0;
                }
                j += 3;
            }
        }
        //Executing Virtual Address Translations
        //Accepting VA 
        
        File input_file = new File("my_input.txt");

        BufferedReader input_br = new BufferedReader(new FileReader(input_file));

        FileWriter writer = new FileWriter("my_output.txt");
        
        //Reading the input
        String input_line;
        ArrayList<Integer> virtual_addresses = new ArrayList<Integer>();

        while((input_line = input_br.readLine()) != null){
            String split_input[] = input_line.split(" ");
            for(int l = 0; l < split_input.length; l++){
                virtual_addresses.add(Integer.parseInt(split_input[l]));
            }
        }

        ArrayList<VA> va_object_list = new ArrayList<VA>();
        for(int k = 0; k < virtual_addresses.size(); k++){
            int va = virtual_addresses.get(k);
            int s, w, p, pw;
            s = w = pw = 0;
            int mask = 0x1FF;
            int mask2 = 0x3FFFF;


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
        /*
        for(int k =2; k < 10; k++){
            System.out.println(physical_memory[k]);
        }
        */

        //create free_frame to all 0
        
        /*
        for(int k =2; k < 16; k++){
            System.out.print(free_frame_list[k]);
        }
        */
        //Translating the virtual addresses
        int pa = 0;
        for(VA obj: va_object_list){
            if(obj.pw >= physical_memory[2 * obj.s]){
                //System.out.print(-1 + " ");
                writer.write(-1 + " ");
                continue;
            }
            if(physical_memory[2 * obj.s + 1] < 0){
                int f1 = 0;

                for(int k = 2; k < free_frame_list.length; k++){
                    if(free_frame_list[k] == 1){
                        f1 = k;
                        free_frame_list[k] = 0;
                        break;
                    }
                }

                for(int k = 0; k < 512; k++){
                    physical_memory[f1 * 512 + k] = paging_disk[-1 * physical_memory[2 * obj.s + 1]][k];
                }

                physical_memory[2 * obj.s + 1] = f1;
            }

            if(physical_memory[physical_memory[2 * obj.s + 1] * 512 + obj.p] < 0){

                int f2 = 0;

                for(int k = 2; k < free_frame_list.length; k++){
                    if(free_frame_list[k] == 1){
                        f2 = k;
                        free_frame_list[k] = 0;
                        break;
                    }
                }

                for(int k = 0; k < 512; k++){
                        physical_memory[f2 * 512 + k] = paging_disk[-1 * physical_memory[physical_memory[2 * obj.s +1] * 512 + obj.p]][k];
                }
                physical_memory[physical_memory[2 * obj.s + 1] * 512 + obj.p] = f2;
            }
            pa  = physical_memory[physical_memory[2 * obj.s + 1] * 512 + obj.p] * 512 + obj.w;
            writer.write(pa + " ");
            //System.out.print(pa + " ");
        }

        br.close();
        input_br.close();
        writer.close();

    }
}