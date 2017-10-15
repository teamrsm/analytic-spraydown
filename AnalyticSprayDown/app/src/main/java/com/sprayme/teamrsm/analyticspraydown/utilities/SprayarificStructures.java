package com.sprayme.teamrsm.analyticspraydown.utilities;

import com.sprayme.teamrsm.analyticspraydown.models.Grade;
import com.sprayme.teamrsm.analyticspraydown.models.Pyramid;
import com.sprayme.teamrsm.analyticspraydown.models.PyramidStepType;
import com.sprayme.teamrsm.analyticspraydown.models.Route;
import com.sprayme.teamrsm.analyticspraydown.models.RouteType;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by climbak on 10/12/17.
 */

public class SprayarificStructures {

    public static Pyramid buildPyramid(List<Route> routes, RouteType type, int height, int stepChangeSize, PyramidStepType stepModifier) {
        List<Route> filteredRoutes = routes.stream()
                .filter((route) -> Objects.nonNull(route))
                .filter((route) -> route.getType() == type)
                .collect(Collectors.toList());

        return new Pyramid(filteredRoutes, height, stepChangeSize, stepModifier);
    }

    public static Pyramid buildPyramid(List<Route> routes, RouteType type, int height, int stepChangeSize, PyramidStepType stepModifier, Grade goal) {
        List<Route> filteredRoutes = routes.stream()
                .filter((route) -> route.getType() == type)
                .collect(Collectors.toList());

        return new Pyramid(filteredRoutes, height, stepChangeSize, stepModifier, goal);
    }
}
