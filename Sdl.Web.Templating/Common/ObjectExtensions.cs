using System;
using System.Collections.Generic;
using System.Linq;

namespace Sdl.Web.Tridion.Common
{
    public static class ObjectExtensions
    {
        public static IEnumerable<TOutput> IfNotNull<TInput, TOutput>(this TInput value, Func<TInput, IEnumerable<TOutput>> getResult)
        {
            // TODO possible compare of value type with null (http://confluence.jetbrains.com/display/ReSharper/Possible+compare+of+value+type+with+null)
            return null != value ? getResult(value) : Enumerable.Empty<TOutput>();
        }

        public static TOutput IfNotNull<TInput, TOutput>(this TInput value, Func<TInput, TOutput> getResult)
        {
            // TODO possible compare of value type with null
            return null != value ? getResult(value) : default(TOutput);
        }

        public static void IfNotNull<TInput>(this TInput value, Action<TInput> action)
        {
            // TODO possible compare of value type with null
            if (null != value)
            {
                action(value);
            }
        }
    }
}
