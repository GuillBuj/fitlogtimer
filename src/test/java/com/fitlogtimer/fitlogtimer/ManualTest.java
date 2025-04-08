package com.fitlogtimer.fitlogtimer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.fitlogtimer.FitlogtimerApplication;
import com.fitlogtimer.service.WorkoutService;
import com.fitlogtimer.service.XlsxService;

@SpringBootApplication
public class ManualTest {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(FitlogtimerApplication.class, args);
        
        try {
            XlsxService xlsxService = context.getBean(XlsxService.class);
            WorkoutService workoutService = context.getBean(WorkoutService.class);
            
            //System.out.println("=== TEST DÉMARRÉ ===");
            // System.out.println(workoutService.createWorkoutsFromXlsxDCHeavyDTOList(
            //     xlsxService.extractDTOs()
            // ));
            workoutService.deleteByTagImport("importH");
            workoutService.createWorkoutsFromXlsxDCHeavyDTOList(xlsxService.extractDTOsHeavySheetRegular());

            workoutService.deleteByTagImport("importV"); //à executer après les importH
            workoutService.createWorkoutsFromXlsxDCVarDTOList(xlsxService.extractDTOsVarSheet());

            workoutService.deleteByTagImport("importL");
            workoutService.createWorkoutsFromXlsxDCLightDTOList(xlsxService.extractDTOsLightSheet());

            workoutService.deleteByTagImport("importDL"); //à executer après les importL
            workoutService.createWorkoutsFromXlsxDeadliftDTOList(xlsxService.extractDTOsDeadliftSheet());            

        } catch (Exception e) {
            System.err.println("Échec du test : " + e.getMessage());
            e.printStackTrace();
        } finally {
            context.close();
            System.exit(0); // Important pour terminer proprement
        }
    }
}