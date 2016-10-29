/**
 * This application creates a Time Table based on a given set of inputs.
 * 
 * @author Pranay Venkatesh
 * @version 1.0
 * 
 */

//headers
import java.util.ArrayList;     //This is used for creating a list of teachers.
import java.util.Scanner;       //Used to take input  
import java.util.HashMap;       //To index teacher's names with their subjects.

import java.io.FileNotFoundException; //Used to catch any instance of file not being found
import java.io.File;                  //Used to find and read the file to take inputs.
import java.io.IOException;           //Used to throw any input/outpute exceptions.
import java.io.PrintWriter;           //Used to write to file 'Tables.txt'


public class schoolSystem{             //Main class.  
    
    HashMap<String,Teacher> teachers = new HashMap<String,Teacher>(); //Teachers who are teaching.
    HashMap<String,Section> sections = new HashMap<String,Section>(); //Classes learning.
    int numCRs;     //Number of classrooms
    int numSessions;    //Number of sessions a week
    int numDaySessions; //Number of session a day
    String [] days = {"Mon", "Tue", "Wed", "Thu", "Fri"};
    Session table[][]; //Main table containing all the classrooms and their sessions.
    Vector[][][] availTeachers;
    ArrayList<String> subjectNames = new ArrayList <String>();
    ArrayList<String> teacherNames = new ArrayList <String>();
    int numTeachers;
    File f = new File ("Inputs.txt");
    int stackSize;
    int currStack;
    boolean done;
    int numDays;
    Scanner sc;
    String CRs[] = {"10A", "09A", "08A", "08B", "07A"};
    
    schoolSystem() throws IOException {     //Initialising instant variables of class schoolSystem
        sc = new Scanner (f);
        numCRs = CRs.length;
        numDays = 5;
        numDaySessions = 5;
        numSessions = numDays * numDaySessions;
        stackSize = 1000;
        currStack = 0;
        table = new Session[numCRs][numSessions];
        availTeachers = new Vector[stackSize][numCRs][numSessions];
        done = false;
    }
    class coord {   //Stores co-ordinates
        int i;
        int j;
        coord (int x, int y)
        {
            i = x;
            j = y;
        }
        coord()
        {
            i = 0;
            j = 0;
        }
    }
    
    enum Day {Mon, Tue, Wed, Thu, Fri}
    //Each teacher has a name and a subject and totalhours she is teaching.
    class Teacher {
        private int totalHours;
        private String name;
        private String subject;
        /**
         * @return String teacher's name
         * @param Nothing.
         */
        String name()
        {
            return this.name;
        }
        /**
         * Adding hours to the teacher's total working hours.
         * @param int number of hours to be added.
         * @return Nothing.
         */
        void addHours (int num)
        {
            totalHours += num;
        }
        /**
         * @return String teacher's subject
         */
        String subject()
        {
            return subject;
        }
        Teacher()    //Default teacher values
        {
            totalHours = 0;
            name = "";
            subject = "";
        }
        Teacher(String n, String sub, int num) //Setting values of name, subject and number of hours
        {
            name = new String(n);
            subject = new String(sub);
            totalHours = num;
            teachers.put(n, this);
            teacherNames.add (n);
        }
    }
    
    class Session {     //Each element in the table is a session.
        Section classRoom;
        Day day;
        int period = 0;
        Teacher teacher;
        Session() {
            teacher = null;
        }
        /**
         * @return boolean if teacher value for the cell is not set
         */
        boolean isSet() {
            return teacher != null;
        }
    }

    class Grade {   //Each grade is given an integer value for number of hours for each subject.
        private HashMap<String, Integer> numHoursPerSub = new HashMap<String, Integer>();
        int grade; 
        /**
         * @param String n name of the grade as a string
         * @return Nothing.
         */
        void setGrade(String n)
        {
            grade = Integer.parseInt(n);
        }
        /**
         * Setting the hours for a particular subject.
         * @param String sub, int num.
         * @return Nothing.
         */
        void setHours(String sub, int num) {
            numHoursPerSub.put(sub,num);
        }
        /**
         * @param String subject
         * @return int number of hours for a particular subject.
         */
        int hours(String sub) {
            return numHoursPerSub.get(sub);
        }
    }
    class Section extends Grade {   //Each section is given a Teacher value for each subject.
        private char section;
        private String name;
        private HashMap<String, Teacher> teacherPerSub = new HashMap<String, Teacher>();
        private HashMap<String, Boolean> isTeaching = new HashMap<String, Boolean>();
        Section(String n)
        {
            setGrade(n.substring(0,n.length()-1));
            section = n.charAt(n.length()-1);
            name = n;
        }
        /**
         *This function sets a teacher for a particular subject.
         *@param String sub, Teacher t, the subject to be set for a teacher
         *@return Nothing.
         */
        void setTeacher (String sub, Teacher t)
        {
            teacherPerSub.put(sub, t);
            isTeaching.put(t.name(), true);
        }
        /**
         * @param String subject
         * @return Teacher corresponding to the subject
         */
        Teacher teacher (String sub)
        {
            return (teacherPerSub.get(sub));
        }
        /**
         * @return the name of the classroom
         */
        String name ()
        {
            return this.name;
        }
        /**
         * Checks if a teacher is teaching.
         * @param Teacher t, check if the teacher is teaching
         * @return boolean true/false depending on whether the teacher is in fact teaching or not.
         */
        boolean teaching(Teacher t) {
            if (this.isTeaching.get(t.name()) == null)
                return false;
            return this.isTeaching.get(t.name());
        }
    }
    class Vector{      //Each vector holds a 32-bit integer to check whether a teacher is a vailable for a paricular session.
        int bitSet;
        boolean visited;
        Vector(coord xy)
        {   
            bitSet = 0;
            for (int i = 0; i<teacherNames.size(); i++)
            {
                bitSet <<= 1;                
                
                if (table[xy.i][xy.j].classRoom.teaching(teachers.get(teacherNames.get(i))))
                     bitSet++;
            }
            visited = false;
        }
        Vector()
        {
            bitSet = 0;
            visited = false;
        }
        Vector (int n)
        {
            bitSet = n;
            visited = false;
        }
        /**
         * Removes all values of the vector passed from the actual vector.
         * @param Vector x Vector value whose bitSet is to be removed
         * @return boolean based on whether the bitSet is equal to 0 after removing
         */
        boolean remove(Vector x)
        {
            bitSet = bitSet &(~x.bitSet);
            return(bitSet==0);
        }
        /**
         * @return int the number of values set to 1 for this Vector
         */
        int num()
        {
            int b = bitSet;
            int count = 0;
            for (int i = 0; i< 32; i++)
            {
                if ((b & 1) == 1)
                    count++;
                b >>= 1;
                if (b == 0)
                    return count;
            }
            return count;
        }
        /**
         * @return int index of teacher teaching this classroom.
         */
        int teacher()
        {
            if (bitSet == 0)
                return -1;
            int b = bitSet;
            int count = 0;
            while ((b&1) != 1)
            {
                b >>= 1;
                count++;
            }
            return (count);
        }
        /**
         * @return int a random teacher's index
         */
        int randTeacher()
        {
            ArrayList<Integer> randList = new ArrayList<Integer>();
            if (bitSet == 0)
                return -1;
            int b = bitSet;
            int count = 0;
            while (count<32 && b!=0)
            {
                if ((b&1) == 1)
                    randList.add(count);
                b >>= 1;
                count++;
            }
            int rand = (int) Math.floor(Math.random()*randList.size());
            return (randList.get(rand));
        }
        /**
         * @return int trimmed vector after removing all the teachers except the teacher to be set.
         */
        int trim()
        {
            int count = randTeacher();
            int b = 1 << count;
            return b;
        }
    }
    /**
     * This method is used to set all the values that are common to every table.
     * For example, when each table is made, the days remain the same.
     * Another task this function performs, is reading the input file.
     * 
     * @return Nothing.
     * @exception IOException on input error.
     * 
     */
    void setInitValues() throws IOException   
    {
           for (int sec=0; sec<numCRs; sec++) {    //Sets each section.
            sections.put(CRs[sec], new Section(CRs[sec]));
        }
        
        for (int i = 0; i < numCRs; i++)    //Sets each value in table to a new Session and sets the classroom
            for (int j = 0; j < numSessions; j++)
            {
                table[i][j] = new Session();
                table[i][j].classRoom = sections.get(CRs[i]);
            }
       
        for (int m=0; m<stackSize; m++)     //Sets each value of vector table stack.
            for (int i = 0; i < numCRs; i++)
                for (int j = 0; j < numSessions; j++)
                {
                    availTeachers[m][i][j] = new Vector();
                }

        for (int l = 0; l<numCRs; l++)  //Setting all the periods.
        {
            for (int m = 0; m<numSessions; m++) {
                table[l][m].period = m % numDaySessions;
                switch(m / numDaySessions) {
                    case 0: 
                        table[l][m].day = Day.Mon;
                        break;
                    case 1:
                        table[l][m].day = Day.Tue;
                        break;
                    case 2:
                        table[l][m].day = Day.Wed;
                        break;
                    case 3:
                        table[l][m].day = Day.Thu;
                        break;
                    case 4:
                        table[l][m].day = Day.Fri;
                        break;
                }
            }
        }
        String line;
        String[] words;
        Teacher t;
        int b;
        int n;
        Section a;
        while(sc.hasNextLine()){        //Takes in input values.
            line = sc.nextLine();
            words = line.split(" ");
            t = teachers.get(words[0]);
            b = Integer.parseInt(words[2]);
            if (t == null)
                t = new Teacher(words[0],words[1],b);
            else t.addHours(b);
            a = sections.get(words[3]);
            a.setTeacher(words[1], t);
            n = Integer.parseInt(words[2]);
            a.setHours(words[1], n);
            
        }
        for (int i = 0; i < numCRs; i++)    //Sets teachers for vector co-ordinates.
            for (int j = 0; j < numSessions; j++)
            {
                coord xy = new coord(i, j);
                availTeachers[currStack][i][j] = new Vector(xy);
            }

    }
    /**
     * This method prints all the individual classroom and teacher time-tables
     * @param boolean x.
     * @return Nothing.
     */
    
    void printTable(boolean x) 
    {
        if (x == true)
        {
        for (int i = 0; i < numCRs; i++)
        {
            int p = 0;
            System.out.println("Time table for class " + CRs[i]);
            System.out.print ("\t");
            for (int q = 1; q <=numDaySessions; q++)
            {
                System.out.print(q + "  " + "\t");
            }
            System.out.println();
            for (int j = 0; j < numDays; j++)
            {
                System.out.print(days[j]);
                for (int k = 0; k < numDaySessions; k++)
                {
                    Teacher t = teachers.get(teacherNames.get(teacherNames.size() -1 - (availTeachers[currStack][i][p].teacher())));
                    System.out.print("\t" + t.subject());
                    p++;
                }
                System.out.println();
            }
        }
        
        for (int t = 0; t < teacherNames.size(); t++)
        {
            int p = 0;
            boolean printed = false;
            System.out.println("Time table for teacher " + teacherNames.get(t));
            System.out.print ("\t");
            for (int k = 1; k <=numDaySessions; k++)
            {
                System.out.print(k + "  " + "\t");
            }
            System.out.println();
            for (int j = 0; j < numDays; j++)
            {
                    System.out.print(days[j]);
                    for (int k = 0; k < numDaySessions; k++)
                    {
                        printed = false;
                        for (int i = 0; i < numCRs; i++)
                        {
                           if ((teacherNames.size()-1-availTeachers[currStack][i][p].teacher()) == t) {
                               System.out.print("\t" + CRs[i]);
                               printed = true;
                               break;
                            }
                        }
                        if (! printed) System.out.print("\t" + "---");
                        p++;
                    }
                    System.out.println();
                }
            System.out.println();
            }
        }
    }
    /**
     * Prints the vector table at the top of the vector stack. Used mainly for testing
     * @param Nothing.
     * @return Nothing.
     */
    void printVectorTable() 
    {
        for (int i = 0; i<numCRs; i++)
        {
            
            for (int j = 0; j<numSessions; j++)
            {
                System.out.print("\t" + availTeachers[currStack][i][j].bitSet);
            }
            System.out.println();
        }  
    }
    /**
     * This method prints the full table constisting of all classrooms and sessions.
     * @param Nothing.
     * @return Nothing.
     */
    void printTable()
    {
            for (int q = 0; q < numSessions; q++)
                System.out.print ("\t" + q);
            System.out.println();
            for (int i = 0; i<numCRs; i++)
            {
                System.out.print (CRs[i]);
                for (int j = 0; j<numSessions; j++)
                {
                    Teacher t = teachers.get(teacherNames.get(teacherNames.size() -1 - (availTeachers[currStack][i][j].teacher())));
                    System.out.print("\t" + t.subject());
                }
                System.out.println();
            }
            printTable(true);
        
    }
    /**
     * This method checks whether all the constraints are met for the particular session set to the co-ordinates.
     * @param coord xy the co-ordinates to be checked
     * @return boolean true/false based on constraints
     */
    boolean prune (coord xy)
    {
        boolean result = false;
        result = remColumn(xy);
        if (result)
            return true;
        result = remRow (xy);
        if (result)
            return true;
        result = pruneMultipleClassesConstraint(xy);
        if (result)
            return true;
        return false;
    }
    /**
     * This method checks whether consecutive classes have the same subject and acts accordingly.
     * @param coord xy the co-ordinate to be checked.
     * @return boolean true/false based on constraint.
     */
    boolean pruneMultipleClassesConstraint(coord xy)
    {
        boolean res = false;
        if (xy.j % numDaySessions < (numDaySessions-1))
        {
            res = availTeachers[currStack][xy.i][xy.j+1].remove (availTeachers[currStack][xy.i][xy.j]);
        }    
        return res;
    }
    /**
     * This method counts the number of cells in the row with the set teacher
     * If it exceeds numHours for this subject, then the teacher is removed from all the unvisited vectors in the row.
     * @param coord xy the co-ordinates to be checked
     * @return boolean true/false based on constraints
     */
    boolean remRow (coord xy)
    {
        //Count the number of cells in this row with this teacher.
        //If count exceeds numHours for this subject, then remove the teacher from all the unvisited in this row.
        int count = 0;
        for (int j = 0; j<numSessions; j++)
            if ((availTeachers[currStack][xy.i][j].bitSet == availTeachers[currStack][xy.i][xy.j].bitSet )&& availTeachers[currStack][xy.i][j].visited)
                count++;
        Teacher temp = (teachers.get(teacherNames.get(teacherNames.size()-1 - availTeachers[currStack][xy.i][xy.j].teacher())));
        int numHours = table[xy.i][xy.j].classRoom.hours(temp.subject);
       
        if (count < numHours)
            return false;
        boolean res = false;
        if (count == numHours)
            for (int j = 0; j<numSessions; j++)
                if (!(availTeachers[currStack][xy.i][j].visited))
                    if (availTeachers[currStack][xy.i][j].remove(availTeachers[currStack][xy.i][xy.j])) res = true;
        return res;
    }
    /**
     * This method checks if a teacher is teaching twice in a particular row and removes all instances of the same.
     * @param coord xy co-ordinates to be checked.
     * @return boolean true/false based on the check.
     */
    boolean remColumn (coord xy) 
    {
        for (int i = 0; i<numCRs; i++)
        {
            if (i == xy.i)
                 continue;
            if (availTeachers[currStack][i][xy.j].remove(availTeachers[currStack][xy.i][xy.j]))
                return true;
        }
        return false;
    }
    /**
     * This method finds a cell that has not been visited and returns the co-ordinates.
     * @param Nothing.
     * @return coord the co-ordinates of the cell found.
     */
    coord findCell ()
    {
        int min = 33;
        ArrayList<coord>temp = new ArrayList<coord>();
        int t;
        boolean tableDone = true;
        for (int i = 0; i<numCRs; i++)
        {
            for (int j =0; j<numSessions; j++)
            {
                if (availTeachers[currStack][i][j].visited)
                    continue;
                t = availTeachers[currStack][i][j].num();
                if (t > 1) {
                    tableDone = false;
                }
                if (min>t)
                {
                    min = t;
                }
                
            }
            
        }
        for (int i = 0; i<numCRs; i++)
            for(int j = 0; j<numSessions; j++)
                if (min == availTeachers[currStack][i][j].num())
                    temp.add(new coord(i,j));
        if (tableDone)
            done = true;
        int rand = (int) Math.floor(Math.random() * temp.size());
        
        return (temp.get(rand));
    }
    /**
     * Moves to the next item on the stack in the 3D vector array.
     * @param coord xy co-ordinates to be checked.
     * @return Nothing.
     */
    void popStack (coord xy)
    {
        if (currStack == 0)
        {
            System.out.println("Fail.");
            System.out.println("Enter new allocations for proper table.");
            System.exit(0);
        }
        currStack -= 1;
    }
    /**
     * Sets a cell and displays the table, if done.
     * @param Nothing.
     * @return Nothing.
     * @exception IOException on input error
     * @see IOException
     */
    void set() throws IOException
    {
        coord xy = findCell();
        if (done) 
        {
            Scanner choice = new Scanner (System.in);
            printTable();
            System.out.println("Write to file? (y/n)");
            char ch = choice.next().charAt(0);
            if (ch == 'y')
            {   
                writeToFile();
                System.out.println("Find file 'Tables.txt' where all the tables are given");
            }
            System.exit(0);
        }
        split(xy);
        if (prune(xy))
            popStack(xy);
    }
    /**
     * Writes all the individual classroom and teacher time-tables in the text file "Tables.txt".
     * @param Nothing.
     * @return Nothing.
     * @exception IOExcpetion on input or output error
     */
    void writeToFile() throws IOException   
    {
        File f = new File("Tables.txt");
        if (!f.createNewFile())
        {
            f.delete();
            f.createNewFile();
        }
        PrintWriter writer = new PrintWriter("Tables.txt", "UTF-8");
        for (int i = 0; i < numCRs; i++)
        {
            int p = 0;
            writer.println("Time table for class " + CRs[i]);
            writer.print ("\t");
            for (int q = 1; q <=numDaySessions; q++)
            {
                writer.print(q + "  " + "\t");
            }
            writer.println();
            for (int j = 0; j < numDays; j++)
            {
                writer.print(days[j]);
                for (int k = 0; k < numDaySessions; k++)
                {
                    Teacher t = teachers.get(teacherNames.get(teacherNames.size() -1 - (availTeachers[currStack][i][p].teacher())));
                    writer.print("\t" + t.subject());
                    p++;
                }
                writer.println();
            }
        }
        
        for (int t = 0; t < teacherNames.size(); t++)
        {
            int p = 0;
            boolean printed = false;
            writer.println ("Time table for teacher " + teacherNames.get(t));
            writer.print("\t");
            for (int k = 1; k <=numDaySessions; k++)
            {
                writer.print(k + "  " + "\t");
            }
            writer.println();
            for (int j = 0; j < numDays; j++)
            {
                    writer.print(days[j]);
                    for (int k = 0; k < numDaySessions; k++)
                    {
                        printed = false;
                        for (int i = 0; i < numCRs; i++)
                        {
                           if ((teacherNames.size()-1-availTeachers[currStack][i][p].teacher()) == t) {
                               writer.print("\t" + CRs[i]);
                               printed = true;
                               break;
                            }
                        }
                        if (! printed) writer.print("\t" + "---");
                        p++;
                    }
                    writer.println();
                }
            writer.println();
        }
        writer.close();
    }
    /**
     * Keeps setting values until a table is set perfectly.
     * @param Nothing.
     * @return Nothing.
     * @exception IOException on input or output error.
     */
    void search() throws IOException
    {
        while (!done)
        {
            set();
        }
    }
    /**
     * Splits a table into two, where one particular cell is given a teacher while the other is devoid of that teacher.
     * @param coord xy co-ordinates where the table is split
     * @return Nothing.
     */
    void split(coord xy)    
    {
        int v = availTeachers[currStack][xy.i][xy.j].trim();
        int nv = availTeachers[currStack][xy.i][xy.j].bitSet & (~v);
        if (nv != 0) {
            int m = currStack + 1;
            for(int i = 0; i<numCRs; i++)
                for (int j = 0; j<numSessions; j++)
                {
                    availTeachers[m][i][j].visited = availTeachers[currStack][i][j].visited;
                    availTeachers[m][i][j].bitSet = availTeachers[currStack][i][j].bitSet;
                }
            availTeachers[m][xy.i][xy.j].bitSet = v;
            availTeachers[currStack][xy.i][xy.j].bitSet = nv;
            currStack = m;
        }
        availTeachers[currStack][xy.i][xy.j].visited = true;    
    }
    /**
     * Displays options for the user.
     * @param Nothing.
     * @return int - value user chose.
     */

    int menu()
    {
        Scanner in = new Scanner(System.in);
        System.out.println ("1)Display time table collection.");
        System.out.println ("2)About ScheduleMaster.");
        System.out.println ("3)Exit.");
        int ch = in.nextInt();
        return (ch);
    }
    /**
     * This is the main method which makes use of all the other methods listed in the program
     * @param args unused.
     * @return Nothing.
     * @exception IOException On input error.
     * @see IOException
     */
    public static void main (String[]args) throws IOException   //Main function
    {
        System.out.println ("ScheduleMaster v1.0 (c) Pranay Venkatesh, Grade 10.");
        schoolSystem nps = new schoolSystem();
        nps.setInitValues();
        int c = nps.menu();
        switch (c)  //Performs differently with different user choice.
        {
            case 1:
            nps.search();
            nps.menu();
            break;
            case 2:
            nps.menu();
            break;
            case 3:
            nps.menu();
            break;
            case 4:
            System.out.println("ScheduleMaster v1.0, Created by Pranay Venkatesh, Grade 10, NPS International Chennai.");
            System.out.println("How to use.");
            System.out.print("Step 1: Find and open 'Inputs.txt' and enter allocations");
            System.out.println(" in the form 'teacher, subject, number of hours, classroom'");
            System.out.println("Step 2: Open the project. Right click on 'schoolSystem' and select 'void main(String[]args)'");
            System.out.println("Step 3: Select the first option presented");
            System.out.println("Step 4: Answer 'y' to keep tables if the values are satisfactory.");
            System.out.println("Step 5: Find the file 'Tables.txt' and open to find the tables.");
            nps.menu();
            break;
            default:
            System.exit(0);
        }
    }  
}
