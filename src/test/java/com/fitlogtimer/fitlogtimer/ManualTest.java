package com.fitlogtimer.fitlogtimer;

//import com.fitlogtimer.repository.ExerciseRepository;
import com.fitlogtimer.FitlogtimerApplication;
import com.fitlogtimer.repository.ExerciseRepository;
import com.fitlogtimer.service.WorkoutService;
import com.fitlogtimer.service.XlsxService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

//import com.fitlogtimer.FitlogtimerApplication;
//import com.fitlogtimer.service.WorkoutService;
//import com.fitlogtimer.service.XlsxService;

import java.io.IOException;

@SpringBootApplication
public class ManualTest {

    public static void main(String[] args) throws IOException {


        ConfigurableApplicationContext context = SpringApplication.run(FitlogtimerApplication.class, args);
        try  {
            WorkoutService workoutService = context.getBean(WorkoutService.class);
            ExerciseRepository exerciseRepository = context.getBean(ExerciseRepository.class);
            XlsxService xlsxService = context.getBean(XlsxService.class);
            workoutService.deleteByTagImport("importMuscu6+");
            workoutService.createWorkoutsFromXlsxGenericDTO(xlsxService.extractGenericSheet("Muscu46 comp"));
//            workoutService.deleteByTagImport("importMuscu9bis");
//            workoutService.createWorkoutsFromXlsxGenericDTO(xlsxService.extractGenericSheet("IMPORT vierge2"));
//            workoutService.deleteByTagImport("importTigerShark");
//            workoutService.createWorkoutsFromXlsxGenericDTO(xlsxService.extractGenericSheet("IMPORT vierge"));
        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution : " + e.getMessage());
            e.printStackTrace();
        } finally {
            context.close();
            System.exit(0); // Important pour terminer proprement
        }




        // ConfigurableApplicationContext context = SpringApplication.run(FitlogtimerApplication.class, args);
        
        // try {
        //     XlsxService xlsxService = context.getBean(XlsxService.class);
        //     WorkoutService workoutService = context.getBean(WorkoutService.class);
            
        //     //System.out.println("=== TEST DÉMARRÉ ===");
        //     // System.out.println(workoutService.createWorkoutsFromXlsxDCHeavyDTOList(
        //     //     xlsxService.extractDTOs()
        //     // ));
        //     workoutService.deleteByTagImport("importH");
        //     workoutService.createWorkoutsFromXlsxDCHeavyDTOList(xlsxService.extractDTOsHeavySheetRegular());

        //     workoutService.deleteByTagImport("importV"); //à executer après les importH
        //     workoutService.createWorkoutsFromXlsxDCVarDTOList(xlsxService.extractDTOsVarSheet());

        //     workoutService.deleteByTagImport("importL");
        //     workoutService.createWorkoutsFromXlsxDCLightDTOList(xlsxService.extractDTOsLightSheet());

        //     workoutService.deleteByTagImport("importDL"); //à executer après les importL
        //     workoutService.createWorkoutsFromXlsxDeadliftDTOList(xlsxService.extractDTOsDeadliftSheet());            

        // } catch (Exception e) {
        //     System.err.println("Échec du test : " + e.getMessage());
        //     e.printStackTrace();
        // } finally {
        //     context.close();
        //     System.exit(0); // Important pour terminer proprement
        // }
    }
}