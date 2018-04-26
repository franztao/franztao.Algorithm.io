function [ output_args ] = PolyTao( input_argx,input_argy,powerNumber,iscurveFitting)
%UNTITLED Summary of this function goes here
%   Detailed explanation goes here
    if iscurveFitting
        polyFunction=polyfit(input_argx(1,2:1:end),input_argy(1,2:1:end),powerNumber);
        output_args=[input_argy(1,1)];
        for i=2:1:(length(input_argx))
            output_args=[output_args,polyval(polyFunction,input_argx(1,i))];
        end
    else
        output_args=input_argy;
    end
    
end

