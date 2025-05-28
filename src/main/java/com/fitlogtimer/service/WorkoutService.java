package com.fitlogtimer.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fitlogtimer.constants.FileConstants;
import com.fitlogtimer.dto.fromxlsx.FromXlsxGenericDTO;
import com.fitlogtimer.dto.fromxlsx.FromXlsxGenericWorkoutDTO;
import com.fitlogtimer.dto.update.WorkoutUpdateDTO;
import com.fitlogtimer.mapper.ExerciseSetFacadeMapper;
import com.fitlogtimer.parser.XlsxReader;
import com.fitlogtimer.util.parser.GenericStrengthWorkoutParser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import static com.fitlogtimer.constants.ExerciseSetConstants.SetTypes.HEAVY;
import static com.fitlogtimer.constants.ExerciseSetConstants.SetTypes.LIGHT_50;
import static com.fitlogtimer.constants.ExerciseSetConstants.SetTypes.MEDIUM_55;

import com.fitlogtimer.constants.ExerciseColorConstants;
import com.fitlogtimer.constants.ExerciseSetType;
import com.fitlogtimer.constants.WorkoutColorConstants;
import com.fitlogtimer.dto.base.SetBasicDTO;
import com.fitlogtimer.dto.base.SetBasicElasticDTO;
import com.fitlogtimer.dto.base.SetBasicInterfaceDTO;
import com.fitlogtimer.dto.base.SetBasicIsometricDTO;
import com.fitlogtimer.dto.base.SetBasicMovementDTO;
import com.fitlogtimer.dto.create.ExerciseSetCreateDTO;
import com.fitlogtimer.dto.create.WorkoutCreateDTO;
import com.fitlogtimer.dto.details.LastSetDTO;
import com.fitlogtimer.dto.details.WorkoutDetailsBrutDTO;
import com.fitlogtimer.dto.details.WorkoutDetailsGroupedDTO;
import com.fitlogtimer.dto.display.CalendarDTO;
import com.fitlogtimer.dto.display.WorkoutListDisplayDTO;
import com.fitlogtimer.dto.listitem.CalendarItemDTO;
import com.fitlogtimer.dto.listitem.SetGroupCleanWorkoutListItemDTO;
import com.fitlogtimer.dto.listitem.SetWorkoutListItemDTO;
import com.fitlogtimer.dto.listitem.WorkoutListItemDTO;
import com.fitlogtimer.dto.transition.SetInWorkoutDTO;
import com.fitlogtimer.dto.transition.SetsGroupedDTO;
import com.fitlogtimer.dto.transition.SetsGroupedWithNameDTO;
import com.fitlogtimer.exception.NotFoundException;
import com.fitlogtimer.mapper.ExerciseSetMapper;
import com.fitlogtimer.mapper.WorkoutMapper;
import com.fitlogtimer.model.Exercise;
import com.fitlogtimer.model.ExerciseSet;
import com.fitlogtimer.model.Workout;
import com.fitlogtimer.model.sets.ElasticSet;
import com.fitlogtimer.model.sets.FreeWeightSet;
import com.fitlogtimer.model.sets.IsometricSet;
import com.fitlogtimer.model.sets.MovementSet;
import com.fitlogtimer.repository.ExerciseRepository;
import com.fitlogtimer.repository.ExerciseSetRepository;
import com.fitlogtimer.repository.WorkoutRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class WorkoutService {

    private final WorkoutRepository workoutRepository;

    private final WorkoutMapper workoutMapper;

    private final ExerciseSetMapper exerciseSetMapper;

    private final ExerciseRepository exerciseRepository;

    private final ExerciseSetService exerciseSetService;

    private final ExerciseSetRepository exerciseSetRepository;

    private final SetsGroupCleanerService setsGroupCleanerService;

    private final GenericStrengthWorkoutParser genericStrengthWorkoutParser;

    private final XlsxService xlsxService;
    private final ExerciseSetFacadeMapper exerciseSetFacadeMapper;

    // public List<WorkoutListDisplayDTO> getAllWorkoutsDisplayDTO() {
    //     List<Workout> workouts = workoutRepository.findAllByOrderByDateDesc();
    //     Map<Integer, List<String>> exercises = getExerciseNamesForWorkouts(workouts);

    //     return workouts.stream()
    //         .map(workout -> workoutMapper.toWorkoutDisplayDTO(
    //             workout,
    //             exercises.getOrDefault(workout.getId(), List.of())
    //     ))
    //         .toList();
    // }

    public Page<WorkoutListDisplayDTO> getPaginatedWorkoutsDisplayDTO(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        Page<Workout> workoutPage = workoutRepository.findAllByOrderByDateDesc(pageable);

        Map<Integer, List<String>> exercises = getExerciseNamesForWorkouts(workoutPage.getContent());

        return workoutPage
            .map(workout -> workoutMapper.toWorkoutDisplayDTO(
                workout,
                exercises.getOrDefault(workout.getId(), List.of())
        ));
    }

    public CalendarDTO getCalendar(){
        
        return new CalendarDTO(getCalendarItems(), ExerciseColorConstants.COLORS, WorkoutColorConstants.COLORS);
    }

    public List<CalendarItemDTO> getCalendarItems() {
        List<Workout> workouts = workoutRepository.findAll(); // ordre pas important ici, c'est JS qui trie par date
        Map<Integer, List<String>> exercises = getExerciseNamesForWorkouts(workouts);
    
        return workouts.stream()
                .map(workout -> new CalendarItemDTO(
                        workout.getId(),
                        workout.getType(),
                        workout.getDate(),
                        exercises.get(workout.getId()))
                )
                .toList();
        }

    // public List<WorkoutListItemDTO> getAllWorkoutsDTO() {
    //     List<Workout> workouts = workoutRepository.findAllByOrderByDateDesc();
    //     Map<Integer, List<String>> exercises = getExerciseNamesForWorkouts(workouts);

    //     return workouts.stream()
    //             .map(workout -> workoutMapper.toWorkoutListItemDTO(
    //                 workout,
    //                 exercises.getOrDefault(workout.getId(), List.of())))
    //             .toList();
    // }
    
    public List<Workout> getAllWorkouts() {
        return workoutRepository.findAllByOrderByDateDesc();
    }
    
    @Transactional
    public WorkoutListItemDTO createWorkout(WorkoutCreateDTO workoutInDTO) {
        log.info("WorkoutCreateDTO: {}", workoutInDTO);
        Workout savedWorkout = workoutRepository.save(workoutMapper.toEntity(workoutInDTO));
        log.info("Saved workout: {}", savedWorkout);
        return workoutMapper.toWorkoutListItemDTO(savedWorkout, getExerciseShortNamesByWorkoutId(savedWorkout.getId()));
    }

    @Transactional
    public boolean deleteWorkout(int workoutId) {
        if (workoutRepository.existsById(workoutId)) {
            workoutRepository.deleteById(workoutId);
            return true;
        }
        return false;
    }

    public WorkoutDetailsBrutDTO getWorkoutDetailsBrut(int id){
        Workout workout = workoutRepository.findById(id)
            .orElseThrow(()->new NotFoundException("Workout not found: " + id));

        List<SetWorkoutListItemDTO> setsDTO = getSetsOutDTO(workout);
        log.info(setsDTO.toString());
        return new WorkoutDetailsBrutDTO(id, workout.getDate(), workout.getBodyWeight(), workout.getComment(), workout.getType(), setsDTO);
    }
    
    public WorkoutDetailsGroupedDTO getWorkoutGrouped(int id){
        Workout workout = workoutRepository.findById(id)
            .orElseThrow(()->new NotFoundException("Workout not found: " + id));

        List<SetsGroupedDTO> groupedSets = groupConsecutiveSetsByExercise(getSetsDTO(workout));

        List<SetsGroupedWithNameDTO> groupedSetsWithName = groupedSets.stream()
                .map(this::groupedSetToGroupedSetWithName)
                .toList();

        List<SetGroupCleanWorkoutListItemDTO> finalGroupedSets = groupedSetsWithName.stream()
                .map(setsGroupCleanerService::cleanSetsGroup)
                .toList();
        
        WorkoutDetailsGroupedDTO workoutGroupedDTO = new WorkoutDetailsGroupedDTO(id, workout.getDate(), workout.getBodyWeight(), workout.getComment(), workout.getType(), finalGroupedSets);
        System.out.println(workoutGroupedDTO);
        return workoutGroupedDTO;
    }
    
    public List<SetsGroupedDTO> groupConsecutiveSetsByExercise(List<SetInWorkoutDTO> exerciseSets){
        List<SetsGroupedDTO> groupedSets = new ArrayList<>();
        List<SetInWorkoutDTO> currentGroup = new ArrayList<>();

        for(int i=0; i < exerciseSets.size(); i++){
            SetInWorkoutDTO currentSet = exerciseSets.get(i);

            if (i==0 || currentSet.exercise_id() == exerciseSets.get(i-1).exercise_id()) {
                currentGroup.add(currentSet);
            } else {
                groupedSets.add(new SetsGroupedDTO(new ArrayList<>(currentGroup)));
                currentGroup.clear();
                currentGroup.add(currentSet);
            }
            log.debug("{} after: groupedSets: {}, current: {}", i, groupedSets, currentGroup);
        }

        if(!currentGroup.isEmpty()){
            groupedSets.add(new SetsGroupedDTO(currentGroup));
        }

        return groupedSets;
    }

    public SetsGroupedWithNameDTO groupedSetToGroupedSetWithName(SetsGroupedDTO entrySet){
        int exerciseId = entrySet.setGroup().get(0).exercise_id();
        Exercise exercise = exerciseRepository.findById(exerciseId)
                            .orElseThrow(()->new NoSuchElementException("Exercise not found with id: " + exerciseId));
        log.info("*-* Exercise: {}", exercise);
        String shortName = exercise.getShortName();
        String exerciseType = exercise.getType();

        List<SetBasicInterfaceDTO> sets = List.of();
        
        if(exerciseType.equals(ExerciseSetType.FREE_WEIGHT) || exerciseType.equals(ExerciseSetType.BODYWEIGHT)){
            //log.info("*-*-* Creating SetBasic");
            sets = entrySet.setGroup().stream()
                    .map(set -> (SetBasicInterfaceDTO) new SetBasicDTO(set.repNumber(), set.weight()))
                    .toList();
        } else if (exerciseType.equals(ExerciseSetType.ELASTIC)){
            //log.info("*-*-* Creating SetBasicElastic");
            sets = entrySet.setGroup().stream()
                    .map(set -> (SetBasicInterfaceDTO) new SetBasicElasticDTO(set.repNumber(), set.bands()))
                    .toList();
        } else if (exerciseType.equals(ExerciseSetType.ISOMETRIC)){
            //log.info("*-*-* Creating SetBasicIsometric");
            sets = entrySet.setGroup().stream()
                    .map(set -> (SetBasicInterfaceDTO) new SetBasicIsometricDTO(set.durationS(), set.repNumber(), set.weight()))
                    .toList();
        } else if (exerciseType.equals(ExerciseSetType.MOVEMENT)){
            //log.info("*-*-* Creating SetBasicMovement");
            sets = entrySet.setGroup().stream()
                    .map(set -> (SetBasicInterfaceDTO) new SetBasicMovementDTO(set.repNumber(), set.distance(), set.bands(), set.weight()))
                    .toList();
        }

        return new SetsGroupedWithNameDTO(shortName, sets);        
    }

    public List<SetInWorkoutDTO> getSetsDTO(Workout workout){
        log.debug("*** getSetDTO ***");
        
        return workout.getSetRecords().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private SetInWorkoutDTO mapToDTO(ExerciseSet exerciseSet) {
        //if (set instanceof FreeWeightSet freeSet) {
            log.debug("***: {}",exerciseSet.toString());
            return exerciseSetMapper.toSetInWorkoutDTO(/*freeS*/exerciseSet);
       // }
    }

    public List<SetWorkoutListItemDTO> getSetsOutDTO(Workout workout){
        return workout.getSetRecords().stream()
                .map(exerciseSetMapper::toSetListItemDTO)
                .collect(Collectors.toList());
    }

    

    public LastSetDTO getLastSetDTO(int id){

        Optional<Workout> optionalWorkout = workoutRepository.findById(id);
        if (optionalWorkout.isEmpty()) {return null;}
    
        Workout workout = optionalWorkout.get();
    
        List<ExerciseSet> sets = workout.getSetRecords();
        if (sets == null || sets.isEmpty()) {return null;}
    
        ExerciseSet lastSet = sets.get(sets.size() - 1);
    
        if (lastSet == null || lastSet.getExercise() == null) {return null;}
    
        return exerciseSetMapper.toLastSetDTO(lastSet);
    }

    public ExerciseSetCreateDTO setFormByLastSetDTO(int id) {

        int defaultExerciseId = id;
        double defaultWeight = 0.0;
        int defaultRepNumber = 0;
        String defaultBands = "-";
        int defaultDuration = 0;
        String defaultDistance = "";
        String defaultTag = "";
        String defaultType = ExerciseSetType.FREE_WEIGHT;
        String defaultComment = "";
    
        Optional<Workout> optionalWorkout = workoutRepository.findById(id);
        if (optionalWorkout.isEmpty()) {
            return new ExerciseSetCreateDTO(defaultExerciseId, defaultWeight, defaultRepNumber, defaultBands, defaultDuration, defaultDistance, defaultTag,defaultComment, id, defaultType);
        }
    
        Workout workout = optionalWorkout.get();
    
        List<ExerciseSet> sets = workout.getSetRecords();
        if (sets == null || sets.isEmpty()) {
            return new ExerciseSetCreateDTO(defaultExerciseId, defaultWeight, defaultRepNumber, defaultBands, defaultDuration, defaultDistance, defaultTag,defaultComment, id, defaultType);
        }
    
        ExerciseSet lastSet = sets.get(sets.size() - 1);
    
        if (lastSet == null || lastSet.getExercise() == null) {
            return new ExerciseSetCreateDTO(defaultExerciseId, defaultWeight, defaultRepNumber, defaultBands, defaultDuration, defaultDistance, defaultTag,defaultComment, id, defaultType);
        }
    
        if (lastSet instanceof FreeWeightSet freeWeightSet) {
            defaultWeight = freeWeightSet.getWeight();
        }

        if (lastSet instanceof ElasticSet elasticSet){
            defaultBands = elasticSet.getBands();
        }

        if (lastSet instanceof IsometricSet isometricSet){
            defaultDuration = isometricSet.getDurationS();
            defaultWeight = isometricSet.getWeight();
        }

        if (lastSet instanceof MovementSet movementSet){
            defaultBands = movementSet.getBands();
            defaultDistance = movementSet.getDistance();
            defaultWeight = movementSet.getWeight();
        }

        return new ExerciseSetCreateDTO(
            lastSet.getExercise().getId(),
            defaultWeight,
            lastSet.getRepNumber(),
            defaultBands,
            defaultDuration,
            defaultDistance,
            defaultTag,
            defaultComment,
            id,
            defaultType
        );
    }

    public List<String> getExerciseShortNamesByWorkoutId(int id){
        return workoutRepository.findExerciseShortNamesByWorkoutId(id);
    }

    private Map<Integer, List<String>> getExerciseNamesForWorkouts(List<Workout> workouts) {
        List<ExerciseSet> sets = exerciseSetRepository.findByWorkoutIdIn(
            workouts.stream().map(Workout::getId).toList()
        );
        return sets.stream()
            .collect(Collectors.groupingBy(
                exerciseSet -> exerciseSet.getWorkout().getId(),
                Collectors.mapping(
                    exerciseSet -> exerciseSet.getExercise().getShortName(),
                    Collectors.toCollection(LinkedHashSet::new) //garde l'ordre en supprimant les doublons
                )
            ))
            .entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> new ArrayList<>(entry.getValue())
            ));
    }

    @Transactional
    public void addExerciseSet(ExerciseSetCreateDTO exerciseSetInDTO){
        exerciseSetService.saveExerciseSet(exerciseSetInDTO);
    }

    public void updateWorkout(WorkoutUpdateDTO workoutUpdateDTO){
        Optional<Workout> optionalWorkout = workoutRepository.findById(workoutUpdateDTO.id());

        optionalWorkout.ifPresent(workout -> {
            workoutMapper.updateWorkoutFromDTO(workoutUpdateDTO,workout);
            workoutRepository.save(workout);
        });
    }

    public WorkoutUpdateDTO getWorkoutUpdateDTO(int id){
        log.info("{}",workoutMapper.toWorkoutUpdateDTO(workoutRepository.findById(id).get()));
        return workoutMapper.toWorkoutUpdateDTO(workoutRepository.findById(id).get());
    }

    @Transactional
    public void deleteByTagImport(String tagImport){
        workoutRepository.deleteByTagImport(tagImport);
    }







    /*
     * CREATE FROM XLS
     */

    public List<Workout> createWorkoutsFromXlsxGenericDTO(FromXlsxGenericDTO dto){
        String name = dto.name();
        log.info("Import from generic sheet: {}",name);
        return dto.workouts().stream()
                .map(workoutDTO -> createWorkoutFromXlsxGenericWorkoutDTO(workoutDTO, name))
                .collect(Collectors.toList());
    }

    //  @Transactional
    //  public List<Workout> createWorkoutsFromXlsxDCHeavyDTOList(List<FromXlsxDCHeavyDTO> dtoList) {
    //      log.info("*** Import from Heavy Sheet ***");
    //      return dtoList.stream()
    //          .map(this::createWorkoutFromXlsxDCHeavyDTO)
    //          .collect(Collectors.toList());
    //  }

    //  @Transactional
    //  public List<Workout> createWorkoutsFromXlsxDCLightDTOList(List<FromXlsxDCLightDTO> dtoList) {
    //      log.info("*** Import from Light Sheet ***");
    //      return dtoList.stream()
    //          .map(this::createWorkoutFromXlsxDCLightDTO)
    //          .collect(Collectors.toList());
    //  }

    //  @Transactional
    //  public List<Workout> createWorkoutsFromXlsxDCVarDTOList(List<FromXlsxDCVarDTO> dtoList) {
    //      log.info("*** Import from Var Sheet ***");
    //      return dtoList.stream()
    //          .map(this::createWorkoutFromXlsxDCVarDTO)
    //          .collect(Collectors.toList());
    //  }

    //  @Transactional
    //  public List<Workout> createWorkoutsFromXlsxDeadliftDTOList(List<FromXlsxDeadliftDTO> dtoList) {
    //      log.info("*** Import from Deadlift Sheet ***");
    //      return dtoList.stream()
    //          .map(this::createWorkoutFromXlsxDeadliftDTO)
    //          .collect(Collectors.toList());
    //  }

    //  @Transactional
    //  public Workout createWorkoutFromXlsxDCHeavyDTO(FromXlsxDCHeavyDTO fromXlsxDCHeavyDTO){
    //      Workout workout = workoutMapper.toEntity(fromXlsxDCHeavyDTO);
    //      int idWorkout = workoutRepository.save(workout).getId();
    //      fromXlsxDCHeavyDTO.sets().stream()
    //          .map(basicSet -> {
    //                  int position = fromXlsxDCHeavyDTO.sets().indexOf(basicSet) + 1; // 1-based index
    //                  String tag;

    //                  if (position <= 3) {
    //                      tag = HEAVY;
    //                  } else if (position == 5) {
    //                      tag = MEDIUM_55;
    //                  } else if (position == 6) {
    //                      tag = LIGHT_50;
    //                  } else if (position == 4) {
    //                      tag = basicSet.weight() > 56 ? HEAVY : MEDIUM_55;
    //                  } else {
    //                      tag = "";
    //                  }

    //              return new ExerciseSetCreateDTO(
    //                  exerciseRepository.findByShortName("DC").getId(),
    //                  basicSet.weight(),
    //                  basicSet.repNumber(),
    //                  tag,
    //                  "",
    //                  idWorkout,
    //                  ExerciseSetType.FREE_WEIGHT
    //              );
    //              })
    //          .forEach(exerciseSetInDTO -> {
    //              addExerciseSet(exerciseSetInDTO);
    //          });

    //          log.info("Workout saved: {}", workoutRepository.getReferenceById(idWorkout));
    //      return workout;
    //  }

    //  @Transactional
    //  public Workout createWorkoutFromXlsxDCLightDTO(FromXlsxDCLightDTO fromXlsxDCLightDTO){
    //      Workout workout = workoutMapper.toEntity(fromXlsxDCLightDTO);
    //      int idWorkout = workoutRepository.save(workout).getId();
    //      //log.info("*** workout {} created", idWorkout);
    //      fromXlsxDCLightDTO.sets().stream()
    //          .map(basicSet -> {
    //              //log.info("**/*//** {}, short: ", basicSet, basicSet.exercise());
    //              return new ExerciseSetCreateDTO(
    //                  exerciseRepository.findByShortName(basicSet.exercise()).getId(),
    //                  basicSet.weight(),
    //                  basicSet.repNumber(),
    //                  "",
    //                  "",
    //                  idWorkout,
    //                  ExerciseSetType.FREE_WEIGHT
    //              );
    //              })
    //          .forEach(exerciseSetInDTO -> {
    //              //log.info("-*-*-*-*-*-*-*-*-*-*{}", exerciseSetInDTO);
    //              addExerciseSet(exerciseSetInDTO);
    //          });

    //      log.info("Workout saved: {}", workoutRepository.getReferenceById(idWorkout));
    //      return workout;
    //  }

    //  @Transactional
    //  public Workout createWorkoutFromXlsxDCVarDTO(FromXlsxDCVarDTO fromXlsxDCVarDTO){
    //      Workout workout = workoutMapper.toEntity(fromXlsxDCVarDTO);

    //      final int idWorkout;
    //      if(!workoutRepository.existsByDateAndTagImport(fromXlsxDCVarDTO.date(), "importH")){
    //          idWorkout = workoutRepository.save(workout).getId();
    //      } else {
    //          idWorkout = workoutRepository.findByDateAndTagImport(fromXlsxDCVarDTO.date(), "importH").getId();
    //          log.info("Using existing workout: {}", idWorkout);
    //      }

    //      //log.info("*** workout {} created", idWorkout);
    //      fromXlsxDCVarDTO.sets().stream()
    //          .map(basicSet -> {
    //              //log.info("**/*//** {}, short: ", basicSet, basicSet.exercise());
    //              return new ExerciseSetCreateDTO(
    //                  exerciseRepository.findByShortName(basicSet.exercise()).getId(),
    //                  basicSet.weight(),
    //                  basicSet.repNumber(),
    //                  "",
    //                  "",
    //                  idWorkout,
    //                  ExerciseSetType.FREE_WEIGHT
    //              );
    //              })
    //          .forEach(exerciseSetInDTO -> {
    //              //log.info("-*-*-*-*-*-*-*-*-*-*{}", exerciseSetInDTO);
    //              addExerciseSet(exerciseSetInDTO);
    //          });

    //      log.info("Workout saved: {}", workoutRepository.getReferenceById(idWorkout));
    //      return workout;
    //  }

    //  @Transactional
    //  public Workout createWorkoutFromXlsxDeadliftDTO(FromXlsxDeadliftDTO fromXlsxDeadliftDTO){
    //      Workout workout = workoutMapper.toEntity(fromXlsxDeadliftDTO);
    //      //int idWorkout = workoutRepository.save(workout).getId();
    //      final int idWorkout;
    //      if(!workoutRepository.existsByDateAndTagImport(fromXlsxDeadliftDTO.date(), "importL")){
    //          idWorkout = workoutRepository.save(workout).getId();
    //          log.info("New workout created: {}", idWorkout);
    //      } else {
    //          idWorkout = workoutRepository.findByDateAndTagImport(fromXlsxDeadliftDTO.date(), "importL").getId();
    //          log.info("Using existing workout: {}", idWorkout);
    //      }

    //      //un seul elément ds le format récupéré
    //      addExerciseSet(new ExerciseSetCreateDTO(
    //                  exerciseRepository.findByShortName("DL").getId(),
    //                  fromXlsxDeadliftDTO.sets().weight(),
    //                  fromXlsxDeadliftDTO.sets().repNumber(),
    //                  "ONE DL",
    //                  "",
    //                  idWorkout,
    //                  ExerciseSetType.FREE_WEIGHT
    //      ));

    //      log.info("Workout saved: {}", workoutRepository.getReferenceById(idWorkout));
    //      return workout;
    //  }


    public Workout createWorkoutFromXlsxGenericWorkoutDTO(FromXlsxGenericWorkoutDTO fromXlsxGenericWorkoutDTO, String name){
        Workout workout = workoutMapper.toEntity(fromXlsxGenericWorkoutDTO, name);

        List<ExerciseSet> sets = fromXlsxGenericWorkoutDTO.sets().stream()
                .map(exerciseSetFacadeMapper::toEntity)
                .peek(set -> set.setWorkout(workout)) // lien bidirectionnel si besoin
                .collect(Collectors.toList());

        workout.setSetRecords(sets);

        Workout savedWorkout = workoutRepository.save(workout);
        log.info("Workout with id {} created with {} sets", savedWorkout.getId(), sets.size());

        return savedWorkout;
    }






    //pas fini
//    @Transactional
//    public List<Workout> createWorkoutsFromGenericSheet() throws IOException {
//        String[][] data = xlsxService.extractGenericSheet();
//        List<Workout> createdWorkouts = new ArrayList<>();
//
//        for (int col = 1; col < data[0].length; col++) {
//            String workoutType = data[0][col];
//
//            Workout workout = new Workout();
//
//            List<ExerciseSetCreateDTO> sets = genericStrengthWorkoutParser.parseColumn(data, col, workout.getId());
//            sets.forEach(exerciseSetService::addExerciseSet);
//
//            createdWorkouts.add(workout);
//        }
//        return createdWorkouts;
//    }


}
