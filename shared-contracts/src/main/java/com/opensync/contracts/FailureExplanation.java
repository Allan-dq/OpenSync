package com.opensync.contracts;
public record FailureExplanation(String summary,String likelyCause,String recommendedAction,double confidence){ }
